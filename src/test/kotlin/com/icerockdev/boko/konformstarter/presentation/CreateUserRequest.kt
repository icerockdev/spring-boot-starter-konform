/*
 * Copyright 2026 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
package com.icerockdev.boko.konformstarter.presentation

data class CreateUserRequest(
    val username: String,
    val email: String,
    val age: Int,
    val address: AddressRequest,
    val providers: List<String>
)
