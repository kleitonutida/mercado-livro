package com.mercadolivro.helper

import com.mercadolivro.enums.BookStatus
import com.mercadolivro.enums.CustomerStatus
import com.mercadolivro.enums.Role
import com.mercadolivro.model.BookModel
import com.mercadolivro.model.CustomerModel
import com.mercadolivro.model.PurchaseModel
import com.mercadolivro.security.UserCustomDetails
import java.math.BigDecimal
import java.util.*

fun buildCustomer(
    id: Int? = null,
    name: String = "customer name",
    email: String = "${UUID.randomUUID()}@email.com",
    password: String = "password",
) = CustomerModel(
    id = id,
    name = name,
    email = email,
    status = CustomerStatus.ATIVO,
    password = password,
    roles = setOf(Role.CUSTOMER),
)

fun buildPurchase(
    id: Int? = null,
    customer: CustomerModel = buildCustomer(),
    books: MutableList<BookModel> = mutableListOf(),
    nfe: String? = UUID.randomUUID().toString(),
    price: BigDecimal = BigDecimal.TEN,
) = PurchaseModel(
    id = id,
    customer = customer,
    books = books,
    nfe = nfe,
    price = price,
)

fun buildBook(
    id: Int? = null,
    name: String = "book name",
    price: BigDecimal = BigDecimal.TEN,
    status: BookStatus = BookStatus.ATIVO,
    customer: CustomerModel = buildCustomer(),
) = BookModel(
    id = id,
    name = name,
    price = price,
    status = status,
    customer = customer,
)

fun buildUserDetails(customerModel: CustomerModel) =
    UserCustomDetails(customerModel = customerModel)
