/*
 * Copyright 2026 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
package com.icerockdev.boko.konformstarter.constraints

import io.konform.validation.Constraint
import io.konform.validation.ValidationBuilder

fun ValidationBuilder<String>.length(min: Int, max: Int): Constraint<String> {
    require(min >= 0 && max >= 0) { IllegalArgumentException("length requires the min and max to be >= 0") }
    require(min < max) { IllegalArgumentException("length requires that the min value be less than the max") }
    return constrain("must contain between $min and $max characters") { it.length in min..max }
}
