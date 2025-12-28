/*
 * Copyright 2026 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
package com.icerockdev.boko.konformstarter

import com.icerockdev.boko.konformstarter.presentation.TestRequest
import com.icerockdev.boko.konformstarter.presentation.TestValidator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ValidatorRegistryTest {

    @Test
    fun `Should find validator by request type`() {
        val registry = ValidatorRegistry(listOf(TestValidator()))

        val validator = registry.findValidatorFor(TestRequest("abc", null, 9))

        assertThat(validator).isNotNull
    }

    @Test
    fun `Should return null if validator not found`() {
        val registry = ValidatorRegistry(emptyList())

        val validator = registry.findValidatorFor(TestRequest("abc", null, 9))

        assertThat(validator).isNull()
    }
}
