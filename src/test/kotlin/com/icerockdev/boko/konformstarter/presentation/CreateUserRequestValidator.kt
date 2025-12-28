/*
 * Copyright 2026 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
package com.icerockdev.boko.konformstarter.presentation

import com.icerockdev.boko.konformstarter.RequestValidator
import com.icerockdev.boko.konformstarter.constraints.length
import io.konform.validation.Validation
import io.konform.validation.constraints.maximum
import io.konform.validation.constraints.minimum
import io.konform.validation.constraints.pattern
import io.konform.validation.constraints.uuid

class CreateUserRequestValidator : RequestValidator<CreateUserRequest> {
    override val validator = Validation.Companion<CreateUserRequest> {
        CreateUserRequest::username {
            length(min = 3, max = 50) hint "Username must be between 3 and 50 characters"
            pattern(Regex("^[a-zA-Z0-9_]+$")) hint "Username can only contain letters, numbers, and underscores"
        }

        CreateUserRequest::email {
            pattern(Regex("^[A-Za-z0-9+_.-]+@(.+)$")) hint "Please provide a valid email address"
        }

        CreateUserRequest::age {
            minimum(18) hint "You must be at least 18 years old"
            maximum(120) hint "Please provide a valid age"
        }

        CreateUserRequest::providers onEach {
            uuid() hint "Please provide a valid uuid"
        }

        CreateUserRequest::address {
            AddressRequest::street {
                length(min = 3, max = 100) hint "Street must be between 3 and 100 characters"
            }
            AddressRequest::city {
                length(min = 2, max = 50) hint "City must be between 2 and 50 characters"
            }
            AddressRequest::zipCode {
                pattern(Regex("^\\d{5}(-\\d{4})?$")) hint "Please provide a valid ZIP code"
            }
        }
    }
}
