package com.demo.conditionalcontrolleradvice.module2

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller2 {

    @GetMapping(value = ["/endpoint2"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun hello(): ResponseEntity<Map<String, String>> {
        return ok(mapOf(
            "message" to "Hello again, World!",
        ))
    }
}