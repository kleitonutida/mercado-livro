package com.mercadolivro.controller.request

import com.mercadolivro.validation.EmailAvailable
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

data class PutCustomerRequest(

    @field:NotEmpty(message = "{customer.name.not_empty}")
    val name: String,

    @field:Email(message = "{customer.email.is_valid}")
    @EmailAvailable(message = "{customer.email.not_available}")
    val email: String,
)
