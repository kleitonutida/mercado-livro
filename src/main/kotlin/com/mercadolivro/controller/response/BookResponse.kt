package com.mercadolivro.controller.response

import com.mercadolivro.enums.BookStatus
import com.mercadolivro.model.CustomerModel
import java.math.BigDecimal

data class BookResponse(
    val id: Int? = null,
    val name: String,
    val price: BigDecimal,
    val status: BookStatus? = null,
    val customer: CustomerModel? = null,
)
