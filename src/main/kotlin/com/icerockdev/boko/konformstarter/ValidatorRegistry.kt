/*
 * Copyright 2026 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
package com.icerockdev.boko.konformstarter

import java.lang.reflect.ParameterizedType

class ValidatorRegistry(validators: List<RequestValidator<*>>) {
    private val registry: Map<Class<*>, RequestValidator<*>> = validators.associateBy {
        val type = it.javaClass.genericInterfaces
            .filterIsInstance<ParameterizedType>()
            .firstOrNull()
            ?.actualTypeArguments
            ?.firstOrNull() as? Class<*>
            ?: error("Cannot determine validator type for ${it.javaClass}")
        type
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> findValidatorFor(target: T): RequestValidator<T>? =
        registry[target::class.java] as? RequestValidator<T>
}
