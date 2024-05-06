# Conditional Controller-Advice

## Problem
When using a module-by-package project structure in a Spring Boot web application, setting up a `@WebMVCTest` for a controller within one module necessitates configuring all `@RestControllerAdvice` components, regardless of whether they are used in that module or not.

### Project Setup
We have a Spring Boot web-application that has the following package structure.

```sh
.
├── Application.kt
├── 📁 library
├── 📁 module1
└── 📁 module2
```

- The library project contains a `@ResponseBodyAdvice` with custom logic.
- This `@ResponseBodyAdvice` is required by controllers in the `module1` package.
- This `@ResponseBodyAdvice` is **not** required in the `module2` package.

### Application code
**1. How does our `@ResponseBodyAdvice` look?**

We have a controller advice that contains a `@Service` component as one of it's dependencies.

```kotlin
@RestControllerAdvice(annotations = [ControlledController::class])
class ResponseBodyAdvice(
    private val service: SomeServiceImplementation
) : AbstractMappingJacksonResponseBodyAdvice() {
    
    override fun beforeBodyWriteInternal(
        bodyContainer: MappingJacksonValue,
        contentType: MediaType,
        returnType: MethodParameter,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ) {
       // logic goes here
    }
}
```

The `@Service` component is required for the response-body advice functionality.

Since the advice is only supposed to be used in specific controllers, we control their application using the `annotations` parameter, with the help of a custom annotation that we define.

```kotlin
annotation class ControlledController
```

```kotlin
@Controller
@ControlledController
class Controller1 {
  // response advice is active in this controller
}

@Controller
class Controller2 { 
  // and not in this one  
}
```

When running the application, we see that the controller-advice is only applied to specific controller instances.

### Test code
We'll create two @WebMVCTest classes, each for a controller.

In our particular use-case, the `@WebMvcTest` is not able to instantiate the required `Service` class (since WebMvcTests don't load the entire context by default). This forces us to implement a mock for the service, in *all* controllers within the application.

This might catch developers off guard, and requires additional boilerplate in all @WebMvcTest test-cases.

### Finer details
One might think that because the @ResponseAdvice was configured to attach itself to only one controller, that the @WebMvcTest for other controllers might not require it to be loaded into the application context.

Unfortunately, that is not how it works. Spring will try to instantiate the @RequestBodyAdvice component first, where it would require the `@Service` implementations to be available.

## Possible Solutions

The solutions are ordered by preference

### Use BeanFactory in the Controller-Advice
The reason why we have to set up a mock in @WebMvcTests is because Spring requires the @Service dependency to be available in the application context during start-up.

We could move this dependency to the run-time of the test with the help of a injected `BeanFactory` in the controller advice.

```kotlin
@RestControllerAdvice(annotations = [ControlledController::class])
class ResponseBodyAdvice(
    private val beanFactory: BeanFactory
) : AbstractMappingJacksonResponseBodyAdvice() {

    override fun beforeBodyWriteInternal(
        bodyContainer: MappingJacksonValue,
        contentType: MediaType,
        returnType: MethodParameter,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ) {
        val service = beanFactory.getBean(ResponseBodyAdviceService::class.java)
        response.headers.set("Custom-Header", service.message())
    }

}
```
With this and the `annotations` parameter of the controller advice, all @WebMvcTests that don't require the `ResponseBodyAdvice` will not require any extra test-setup. In-case the controller under test requires this controller advice, then they get their dependencies resolved during run-time.

Since `@RestControllerAdvice` components are always tested with a Spring Application Context, dependency-injection of the `@Service` component may not be required here.

### Use a @Configuration class
A specific @Configuration class can be used in tests that do not require the instantiation of the service-class mocks in the tests themselves. This is much like how we exclude auto-configuration classes from some libraries (like Spring Security) in tests across the application.

### Use @SpringBootTest instead
Depending on your particular project setup, this might not always be the easiest option. The boiler-plate that is required might be more than just setting up a mock for the `@RestControllerAdvice`

## Other solutions that did not work

### Using a @ConditionalOnBean
I tried to use a `@ConditionalOnBean(annotation="")` to load the controller-advice only if there are controllers annotated with the custom-annotation we have.

Since a `@WebMvcTest` will load the controller into the application-context, and since this controller may or may not be annotated with our custom annotation, this seemed like a good way to selectively load the controller-advice into the spring context.

However, this does not work. This is because Spring will only consider beans that have already been loaded, when it looks for the conditional annotation. The controllers themselves will not be loaded into the context before the controller-advice, and hence this strategy does not work as expected.

I also tried to create a custom `@Condition` but had a similar result.

## References
https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/condition/ConditionalOnBean.html

