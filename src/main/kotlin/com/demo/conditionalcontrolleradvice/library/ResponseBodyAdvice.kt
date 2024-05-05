package com.demo.conditionalcontrolleradvice.library

import org.springframework.beans.factory.BeanFactory
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice


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


