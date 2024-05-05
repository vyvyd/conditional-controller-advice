package com.demo.conditionalcontrolleradvice.library

import org.springframework.stereotype.Component

@Component
class ResponseBodyAdviceService {
    fun message () = "Added By Controller-Advice"
}