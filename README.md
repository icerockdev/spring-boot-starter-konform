# Spring Boot Starter Konform

[![Maven Central](https://img.shields.io/maven-central/v/com.icerockdev.boko/spring-boot-starter-konform.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.icerockdev.boko/spring-boot-starter-konform)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A Spring Boot Starter for seamless integration with [Konform](https://github.com/konform-kt/konform) validation library, providing a type-safe and flexible way to validate your request DTOs in Spring Boot applications.

## Features

- Type-safe validation using Konform's DSL
- Customizable error response format
- Support for nested object validation
- Integration with Spring's validation infrastructure

## Installation

Add the following dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.icerockdev.boko:spring-boot-starter-konform:0.1.2")
}
```

## Usage

### 1. Create a Request DTO

```kotlin
data class CreateUserRequest(
    val username: String,
    val email: String,
    val age: Int,
    val address: AddressRequest,
    val providers: List<String>
)

data class AddressRequest(
    val street: String,
    val city: String,
    val zipCode: String
)

```

### 2. Create a Validator

```kotlin
class CreateUserRequestValidator : RequestValidator<CreateUserRequest> {
    override val validator = Validation.Companion<CreateUserRequest> {
        CreateUserRequest::username {
            length(min = 3, max = 50) hint "Username must be between 3 and 50 characters"
            pattern(Regex("^[a-zA-Z0-9_]+$")) hint "Username can only contain letters, numbers, and underscores"
        }

        CreateUserRequest::email {
            pattern(Regex("^[A-Za-z0-9+_.-]+@(.+)$")) hint "Please provide a valid email address"
        }

        CreateUserRequest::age {
            minimum(18) hint "You must be at least 18 years old"
            maximum(120) hint "Please provide a valid age"
        }

        CreateUserRequest::providers onEach {
            uuid() hint "Please provide a valid uuid"
        }

        CreateUserRequest::address {
            AddressRequest::street {
                length(min = 3, max = 100) hint "Street must be between 3 and 100 characters"
            }
            AddressRequest::city {
                length(min = 2, max = 50) hint "City must be between 2 and 50 characters"
            }
            AddressRequest::zipCode {
                pattern(Regex("^\\d{5}(-\\d{4})?$")) hint "Please provide a valid ZIP code"
            }
        }
    }
}
```

### 3. Use in a Controller

```kotlin
@RestController
@RequestMapping("/api/users")
open class UserController {

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    open fun createUser(
        @RequestBody @KonformValidated request: CreateUserRequest
    ): ResponseEntity<*> {
        return ResponseEntity.ok(object {
            val status = "OK"
            val request = request
        })
    }
}
```

## Customization

### Custom Error Response

You can customize the error response by creating a `@ControllerAdvice`:

```kotlin
@ControllerAdvice
class GlobalExceptionHandler {
    
    @ExceptionHandler(KonformValidationException::class)
    fun handleKonformValidationException(
        exception: KonformValidationException,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errors = exception.errors.map {
            mapOf(
                "field" to it.field,
                "message" to it.message,
                "timestamp" = Instant.now(),
            )
        }
        
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(mapOf("errors" to errors))
    }
}
```

## Contributing

All development (both new features and bug fixes) is performed in the `develop` branch. This way `master` always contains the sources of the most recently released version. Please send PRs with bug fixes to the `develop` branch. Documentation fixes in the markdown files are an exception to this rule. They are updated directly in `master`.

The `develop` branch is pushed to `master` on release.

For more details on contributing please see the [contributing guide](CONTRIBUTING.md).

## License

```
Copyright 2026 IceRock MAG Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Credits

- [Konform](https://github.com/konform-kt/konform) - Type-safe validations for Kotlin
- [Spring Boot](https://spring.io/projects/spring-boot) - The web framework used
