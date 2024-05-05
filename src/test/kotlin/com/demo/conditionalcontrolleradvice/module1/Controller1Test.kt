package com.demo.conditionalcontrolleradvice.module1

import com.demo.conditionalcontrolleradvice.library.ResponseBodyAdviceService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(Controller1::class)
class Controller1Test {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var service: ResponseBodyAdviceService

    @Test
    fun testHelloWorld1() {
        every { service.message() } returns "mocked-header-value"

        mockMvc.get("/endpoint1")
            .andExpect {
                status { isOk() }
                content { string(containsString("Hello, World!")) }
                header { string("Custom-Header", "mocked-header-value")}
            }
    }
}