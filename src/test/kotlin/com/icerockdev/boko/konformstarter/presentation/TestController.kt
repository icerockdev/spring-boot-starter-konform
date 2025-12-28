/*
 * Copyright 2026 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
package com.icerockdev.boko.konformstarter.presentation

import com.icerockdev.boko.konformstarter.KonformValidated
import com.icerockdev.boko.konformstarter.KonformValidationException
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
open class TestController {

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    open fun test(
        @RequestBody @KonformValidated 
        request: TestRequest
    ): ResponseEntity<*> {
        return ResponseEntity.ok(object {
            val status = "OK"
            val request = request
        })
    }

    @ExceptionHandler(KonformValidationException::class)
    open fun exceptionHandler(exception: KonformValidationException): ResponseEntity<*> {
        return ResponseEntity.unprocessableEntity().body(
            exception.errors
        )
    }
}
