package com.mercadolivro.exception

import com.mercadolivro.controller.response.ErrorResponse
import com.mercadolivro.controller.response.FieldErrorResponse
import com.mercadolivro.enums.Errors.ML000
import com.mercadolivro.enums.Errors.ML001
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class ControllerAdvice {

    @ExceptionHandler(NotFoundException::class)
    fun handlerNotFoundException(ex: NotFoundException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            httpCode = NOT_FOUND.value(),
            message = ex.message,
            internalCode = ex.errorCode,
            null,
        )
        return ResponseEntity(error, NOT_FOUND)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handlerBadRequestException(ex: BadRequestException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            httpCode = BAD_REQUEST.value(),
            message = ex.message,
            internalCode = ex.errorCode,
            null,
        )
        return ResponseEntity(error, BAD_REQUEST)
    }

    @ExceptionHandler(BookStatusInvalidException::class)
    fun handlerBookStatusInvalidException(
        ex: BookStatusInvalidException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            httpCode = BAD_REQUEST.value(),
            message = ex.message,
            internalCode = ex.errorCode,
            null,
        )
        return ResponseEntity(error, BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handlerMethodArgumentNotValidException(
        ex: MethodArgumentNotValidException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            httpCode = UNPROCESSABLE_ENTITY.value(),
            message = ML001.message,
            internalCode = ML001.code,
            ex.bindingResult.fieldErrors
                .map {
                    FieldErrorResponse(
                        it.defaultMessage ?: "invalid",
                        it.field,
                    )
                },
        )
        return ResponseEntity(error, UNPROCESSABLE_ENTITY)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handlerAccessDeniedException(ex: AccessDeniedException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            httpCode = FORBIDDEN.value(),
            message = ML000.message,
            internalCode = ML000.code,
            null,
        )
        return ResponseEntity(error, BAD_REQUEST)
    }
}
