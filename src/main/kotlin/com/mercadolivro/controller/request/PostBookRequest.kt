package com.mercadolivro.controller.request

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class PostBookRequest(

    @field:NotEmpty(message = "Nome deve ser informado!")
    var name: String,

    @field:NotNull(message = "Preço deve ser informado!")
    @field:Min(value = 1, message = "O valor não pode ser menor ou igual a zero.")
    var price: BigDecimal,

    @JsonAlias("customer_id")
    var customerId: Int,
)
