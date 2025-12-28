/*
 * Copyright 2026 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
package com.icerockdev.boko.konformstarter

import io.konform.validation.Invalid
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature

@Aspect
class KonformValidationAspect(
    private val registry: ValidatorRegistry
) {

    @Before("execution(* *(.., @com.icerockdev.boko.konformstarter.KonformValidated (*), ..))")
    fun validateAnnotatedArgs(joinPoint: JoinPoint) {
        val method = (joinPoint.signature as MethodSignature).method
        val args = joinPoint.args
        val params = method.parameters

        params.forEachIndexed { i, param ->
            if (param.isAnnotationPresent(KonformValidated::class.java)) {
                val arg = args[i]
                val validator = registry.findValidatorFor(arg) ?: return@forEachIndexed
                val result = validator.validate(arg)
                if (result is Invalid) {
                    val errors = result.errors.map {
                        FieldError(it.dataPath, it.message)
                    }
                    throw KonformValidationException(errors)
                }
            }
        }
    }
}
