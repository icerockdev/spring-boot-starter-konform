/*
 * Copyright 2026 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
package com.icerockdev.boko.konformstarter

import io.konform.validation.Validation
import io.konform.validation.ValidationResult

interface RequestValidator<T : Any> {
    val validator: Validation<T>
    fun validate(target: T): ValidationResult<T> = validator(target)
}
