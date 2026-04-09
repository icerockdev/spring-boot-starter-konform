package com.icerockdev.boko.konformstarter

interface KonformValidationMessageFormatter {
    fun getMessage(code: String, userContext: Any?): String
}
