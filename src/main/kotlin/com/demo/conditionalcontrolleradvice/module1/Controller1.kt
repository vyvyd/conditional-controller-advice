package com.demo.conditionalcontrolleradvice.module1

import com.demo.conditionalcontrolleradvice.library.ControlledController
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping



@Controller
@ControlledController
class Controller1 {

    @GetMapping(value = ["/endpoint1"], produces = ["application/json"])
    fun hello(): ResponseEntity<Map<String, String>> {
        return ok(mapOf(
            "message" to "Hello, World!",
        ))
    }
}