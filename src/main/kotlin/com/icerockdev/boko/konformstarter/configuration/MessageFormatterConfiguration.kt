package com.icerockdev.boko.konformstarter.configuration

import com.icerockdev.boko.konformstarter.KonformValidationMessageFormatter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class MessageFormatterConfiguration {
    @Bean
    @ConditionalOnMissingBean
    open fun konformValidationMessageFormatter(): KonformValidationMessageFormatter {
        return object : KonformValidationMessageFormatter {
            override fun getMessage(code: String, userContext: Any?): String {
                return code
            }
        }
    }
}
