/*
 * Copyright 2026 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
package com.icerockdev.boko.konformstarter

import com.fasterxml.jackson.databind.ObjectMapper
import com.icerockdev.boko.konformstarter.common.TestApplication
import com.icerockdev.boko.konformstarter.configuration.MessageFormatterConfiguration
import com.icerockdev.boko.konformstarter.presentation.AddressRequest
import com.icerockdev.boko.konformstarter.presentation.CreateUserRequest
import com.icerockdev.boko.konformstarter.presentation.CreateUserRequestValidator
import com.icerockdev.boko.konformstarter.presentation.UserController
import java.util.UUID
import org.hamcrest.CoreMatchers.hasItem
import org.junit.jupiter.api.Test
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
        UserController::class,
        CreateUserRequestValidator::class,
        MessageFormatterConfiguration::class,
    ]
)
@AutoConfigureMockMvc
class KonformConstraintsIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `Invalid request should return 422`() {
        val request = CreateUserRequest(
            username = "J!",
            email = "uiwdu",
            age = 16,
            providers = listOf(UUID.randomUUID().toString(), "dassd"),
            address = AddressRequest(
                street = "MA",
                city = "A",
                zipCode = "333"
            )
        )

        mockMvc.request(HttpMethod.POST, "/api/users") {
            content = objectMapper.writeValueAsString(request)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnprocessableEntity() }
            content {
                jsonPath("$") { isArray() }

                jsonPath("$[*].field") {
                    value(hasItem(".username"))
                    value(hasItem(".email"))
                    value(hasItem(".age"))
                    value(hasItem(".providers[1]"))
                    value(hasItem(".address.street"))
                    value(hasItem(".address.city"))
                    value(hasItem(".address.zipCode"))
                }

                jsonPath("$[?(@.field == '.username')].message") {
                    value(hasItem("Username can only contain letters, numbers, and underscores"))
                    value(hasItem("Username must be between 3 and 50 characters"))
                }
                jsonPath("$[?(@.field == '.email')].message") {
                    value(hasItem("Please provide a valid email address"))
                }
                jsonPath("$[?(@.field == '.age')].message") {
                    value(hasItem("You must be at least 18 years old"))
                }
                jsonPath("$[?(@.field == '.providers[1]')].message") {
                    value(hasItem("Please provide a valid uuid"))
                }
                jsonPath("$[?(@.field == '.address.street')].message") {
                    value(hasItem("Street must be between 3 and 100 characters"))
                }
                jsonPath("$[?(@.field == '.address.city')].message") {
                    value(hasItem("City must be between 2 and 50 characters"))
                }
                jsonPath("$[?(@.field == '.address.zipCode')].message") {
                    value(hasItem("Please provide a valid ZIP code"))
                }
            }
        }
    }

    @Test
    fun `Valid request should pass`() {
        val request = CreateUserRequest(
            username = "james",
            email = "james@example.com",
            age = 25,
            providers = listOf(UUID.randomUUID().toString(), UUID.randomUUID().toString()),
            address = AddressRequest(
                street = "5th Avenue",
                city = "New York",
                zipCode = "10001"
            )
        )

        mockMvc.request(HttpMethod.POST, "/api/users") {
            content = objectMapper.writeValueAsString(request)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
            }
    }
}
