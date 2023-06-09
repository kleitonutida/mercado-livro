package com.mercadolivro.enums

enum class Errors(val code: String, val message: String) {

    ML000("ML-000", "Access Denied"),

    ML001("ML-001", "Invalid Request"),
    ML002("ML-002", "Invalid Book Status"),

    ML101("ML-101", "Book [%S] not exists"),
    ML102("ML-102", "Cannot update book with status [%s]"),

    ML201("ML-201", "Customer [%s] not exists"),
}
