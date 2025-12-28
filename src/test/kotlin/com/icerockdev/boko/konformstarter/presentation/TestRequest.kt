/*
 * Copyright 2026 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
package com.icerockdev.boko.konformstarter.presentation

data class TestRequest(
    val requiredString: String,
    val optionalString: String? = null,
    val requiredNumber: Int,
)
