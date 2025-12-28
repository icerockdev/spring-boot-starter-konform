/*
 * Copyright 2026 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
package com.icerockdev.boko.konformstarter

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

@AutoConfiguration
@Import(KonformValidationAspect::class)
open class KonformValidationAutoConfiguration {

    @Bean
    open fun validatorRegistry(validators: List<RequestValidator<*>>): ValidatorRegistry {
        return ValidatorRegistry(validators)
    }
}
