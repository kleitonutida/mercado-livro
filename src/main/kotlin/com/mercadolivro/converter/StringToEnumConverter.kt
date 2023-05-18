package com.mercadolivro.converter

import com.mercadolivro.enums.BookStatus
import com.mercadolivro.enums.Errors
import com.mercadolivro.exception.BookStatusInvalidException
import org.springframework.core.convert.converter.Converter

class StringToEnumConverter : Converter<String, BookStatus> {

    override fun convert(value: String): BookStatus? {
        try {
            if (!value.isNullOrEmpty()) {
                return BookStatus.valueOf(value.uppercase())
            } else {
                throw BookStatusInvalidException(Errors.ML002.code, Errors.ML002.message)
            }
        } catch (e: Exception) {
            throw BookStatusInvalidException(Errors.ML002.code, Errors.ML002.message)
        }
    }
}
