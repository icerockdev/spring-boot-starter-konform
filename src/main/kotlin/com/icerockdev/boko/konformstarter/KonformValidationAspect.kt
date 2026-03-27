/*
 * Copyright 2026 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
package com.icerockdev.boko.konformstarter

import io.konform.validation.Invalid
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

@Aspect
class KonformValidationAspect(
    private val registry: ValidatorRegistry,
    private val konformValidationMessageFormatter: KonformValidationMessageFormatter,
) {

    @Before("execution(* *(.., @com.icerockdev.boko.konformstarter.KonformValidated (*), ..))")
    fun validateAnnotatedArgs(joinPoint: JoinPoint) {
        val method = (joinPoint.signature as MethodSignature).method
        val args = joinPoint.args
        val params = method.parameters

        params.forEachIndexed { i, _ ->
            val hasAnnotation = hasKonformValidatedAnnotationWithCache(method, i)

            if (hasAnnotation) {
                val arg = args[i]
                val validator = registry.findValidatorFor(arg) ?: return@forEachIndexed
                val result = validator.validate(arg)
                if (result is Invalid) {
                    val errors = result.errors.map {
                        FieldError(
                            it.dataPath,
                            konformValidationMessageFormatter.getMessage(it.message, it.userContext)
                        )
                    }
                    throw KonformValidationException(errors)
                }
            }
        }
    }

    private val methodsCache = ConcurrentHashMap<Pair<Method, Int>, Boolean>()

    private fun hasKonformValidatedAnnotationWithCache(method: Method, index: Int): Boolean {
        return methodsCache.computeIfAbsent(method to index) {
            hasKonformValidatedAnnotation(method, index)
        }
    }

    private fun hasKonformValidatedAnnotation(method: Method, index: Int): Boolean {
        if (method.parameters[index].isAnnotationPresent(KonformValidated::class.java)) return true

        for (iface in method.declaringClass.interfaces) {
            val ifaceMethod = iface.methods.firstOrNull {
                it.name == method.name &&
                    it.parameterCount == method.parameterCount
            } ?: continue

            if (ifaceMethod.parameters[index].isAnnotationPresent(KonformValidated::class.java)) {
                return true
            }
        }

        return false
    }
}
