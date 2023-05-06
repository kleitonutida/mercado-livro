package com.mercadolivro.service

import com.mercadolivro.enums.BookStatus
import com.mercadolivro.model.BookModel
import com.mercadolivro.repository.BookRepository
import org.springframework.stereotype.Service

@Service
class BookService(
    val bookRepository: BookRepository,
) {
    fun create(book: BookModel) =
        bookRepository.save(book)

    fun findAll(): List<BookModel> =
        bookRepository.findAll().toList()

    fun findByActivies(): List<BookModel> =
        bookRepository.findByStatus(BookStatus.ATIVO)

    fun findById(id: Int): BookModel =
        bookRepository.findById(id).orElseThrow()

    fun delete(id: Int) {
        val book = findById(id)
        book.status = BookStatus.CANCELADO
        update(book)
    }

    fun update(book: BookModel) =
        bookRepository.save(book)
}
