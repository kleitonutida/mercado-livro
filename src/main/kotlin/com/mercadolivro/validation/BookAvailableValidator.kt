package com.mercadolivro.validation

import com.mercadolivro.service.BookService
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class BookAvailableValidator(var bookService: BookService) : ConstraintValidator<BookAvailable, Set<Int>> {
    override fun isValid(value: Set<Int>?, context: ConstraintValidatorContext?): Boolean {
        if (value.isNullOrEmpty()) {
            return false
        }
        return bookService.booksAvailable(value)
    }
}
