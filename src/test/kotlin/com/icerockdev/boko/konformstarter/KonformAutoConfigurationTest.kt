/*
 * Copyright 2026 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
package com.icerockdev.boko.konformstarter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner

class KonformAutoConfigurationTest {

    private val contextRunner = ApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(KonformValidationAutoConfiguration::class.java)
        )

    @Test
    fun `Auto configuration creates required beans`() {
        contextRunner.run { context ->
            assertThat(context).hasSingleBean(ValidatorRegistry::class.java)
            assertThat(context).hasSingleBean(KonformValidationAspect::class.java)
        }
    }
}
