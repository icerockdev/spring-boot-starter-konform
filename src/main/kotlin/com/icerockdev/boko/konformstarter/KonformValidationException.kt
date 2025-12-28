/*
 * Copyright 2026 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
package com.icerockdev.boko.konformstarter

class KonformValidationException(val errors: List<FieldError>) : RuntimeException(
    errors.joinToString("; ") { "${it.field}: ${it.message}" }
)
