package com.mercadolivro.controller.request

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class PostBookRequest(

    @field:NotEmpty(message = "{book.name.not_empty}")
    var name: String,

    @field:NotNull(message = "{book.price.not_null}")
    @field:Min(value = 1, message = "{book.price.value_min}")
    var price: BigDecimal,

    @JsonAlias("customer_id")
    var customerId: Int,
)
