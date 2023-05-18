package com.mercadolivro.exception

class BookStatusInvalidException(
    override val message: String,
    val errorCode: String,
) : Exception()
