/*
 * Copyright 2026 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
package com.icerockdev.boko.konformstarter.presentation

import com.icerockdev.boko.konformstarter.RequestValidator
import io.konform.validation.Validation
import io.konform.validation.constraints.exclusiveMaximum
import io.konform.validation.constraints.maxLength
import io.konform.validation.constraints.minLength

class TestValidator : RequestValidator<TestRequest> {
    override val validator = Validation.Companion {
        TestRequest::requiredString {
            minLength(3) hint "The value must consist of at least 3 characters."
        }

        TestRequest::optionalString ifPresent {
            maxLength(2) hint "The value must consist of less than 3 characters."
        }

        TestRequest::requiredNumber {
            exclusiveMaximum(10) hint "The value must be less than 10."
        }
    }

    override fun validate(target: TestRequest) = validator(target)
}
