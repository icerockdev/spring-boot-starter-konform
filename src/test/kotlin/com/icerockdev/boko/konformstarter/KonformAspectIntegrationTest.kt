/*
 * Copyright 2026 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
package com.icerockdev.boko.konformstarter

import com.fasterxml.jackson.databind.ObjectMapper
import com.icerockdev.boko.konformstarter.common.TestApplication
import com.icerockdev.boko.konformstarter.configuration.MessageFormatterConfiguration
import com.icerockdev.boko.konformstarter.presentation.TestController
import com.icerockdev.boko.konformstarter.presentation.TestRequest
import com.icerockdev.boko.konformstarter.presentation.TestValidator
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request

@SpringBootTest
@ImportAutoConfiguration(KonformValidationAutoConfiguration::class)
@ContextConfiguration(
    classes = [
        TestApplication::class,
        TestController::class,
        TestValidator::class,
        MessageFormatterConfiguration::class,
    ]
)
@AutoConfigureMockMvc
class KonformAspectIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    companion object {
        @JvmStatic
        private fun invalidRequests() = listOf(
            TestRequest("a", null, 9) to FieldError(".requiredString", "The value must consist of at least 3 characters."),
            TestRequest("abc", "USD", 9) to FieldError(".optionalString", "The value must consist of less than 3 characters."),
            TestRequest("abc", null, 10) to FieldError(".requiredNumber", "The value must be less than 10.")
        )

        @JvmStatic
        private fun validRequests() = listOf(
            TestRequest("abc", null, 9),
            TestRequest("abcd", "US", -1)
        )
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    fun `Invalid request should return 422`(input: Pair<TestRequest, FieldError>) {
        mockMvc.request(HttpMethod.POST, "/test") {
            content = objectMapper.writeValueAsString(input.first)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnprocessableEntity() }
            content {
                jsonPath("$[0].message") {
                    value(input.second.message)
                }
                jsonPath("$[0].field") {
                    value(input.second.field)
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("validRequests")
    fun `Valid request should pass`(input: TestRequest) {
        mockMvc.request(HttpMethod.POST, "/test") {
            content = objectMapper.writeValueAsString(input)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
            }
    }
}
