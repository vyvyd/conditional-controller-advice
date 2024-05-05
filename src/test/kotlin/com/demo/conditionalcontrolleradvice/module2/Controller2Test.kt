package com.demo.conditionalcontrolleradvice.module2

import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(Controller2::class)
class Controller2Test {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun testHelloWorld2() {
        mockMvc.get("/endpoint2")
            .andExpect {
                status { isOk() }
                content { string(containsString("Hello again, World!")) }
            }
    }
}