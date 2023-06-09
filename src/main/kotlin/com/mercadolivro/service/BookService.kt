package com.mercadolivro.service

import com.mercadolivro.enums.BookStatus
import com.mercadolivro.enums.Errors
import com.mercadolivro.exception.NotFoundException
import com.mercadolivro.model.BookModel
import com.mercadolivro.model.CustomerModel
import com.mercadolivro.repository.BookRepository
import com.mercadolivro.repository.CustomerRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class BookService(
    private val customerRepository: CustomerRepository,
    private val bookRepository: BookRepository,
) {
    fun create(book: BookModel) =
        bookRepository.save(book)

    fun findAll(pageable: Pageable): Page<BookModel> =
        bookRepository.findAll(pageable)

    fun findByActivies(pageable: Pageable): Page<BookModel> =
        bookRepository.findByStatus(BookStatus.ATIVO, pageable)

    fun findById(id: Int): BookModel =
        bookRepository.findById(id)
            .orElseThrow { NotFoundException(Errors.ML101.message.format(id), Errors.ML101.code) }

    fun delete(id: Int) {
        val book = findById(id)
        book.status = BookStatus.CANCELADO
        update(book)
    }

    fun update(book: BookModel) =
        bookRepository.save(book)

    fun deleteByCustomer(customer: CustomerModel) {
        val books = bookRepository.findByCustomer(customer)
        for (book in books) {
            book.status = BookStatus.DELETADO
        }
        bookRepository.saveAll(books)
    }

    fun findAllByIds(bookIds: Set<Int>): List<BookModel> {
        return bookRepository.findAllById(bookIds).toList()
    }

    fun purchase(books: MutableList<BookModel>) {
        books.map { it.status = BookStatus.VENDIDO }
        bookRepository.saveAll(books)
    }

    fun booksAvailable(bookIds: Set<Int>): Boolean {
        val books = findAllByIds(bookIds)
        val booksNotAvailable = books.filter { it.status !== BookStatus.ATIVO }.toSet()
        return booksNotAvailable.isEmpty()
    }

    fun findByCustomerId(id: Int, pageable: Pageable): Page<BookModel> {
        val customer = customerRepository.findById(id)
            .orElseThrow { NotFoundException(Errors.ML201.message.format(id), Errors.ML201.code) }
        return bookRepository.findByCustomer(customer, pageable)
    }

    fun findByCustomerIdAndBookStatus(pageable: Pageable, id: Int, status: BookStatus): Page<BookModel> {
        val customer = customerRepository.findById(id)
            .orElseThrow { NotFoundException(Errors.ML201.message.format(id), Errors.ML201.code) }
        return bookRepository.findByCustomerAndStatus(customer, status, pageable)
    }
}
