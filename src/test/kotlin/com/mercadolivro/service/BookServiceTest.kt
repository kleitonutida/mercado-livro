package com.mercadolivro.service

import com.mercadolivro.enums.BookStatus
import com.mercadolivro.exception.NotFoundException
import com.mercadolivro.helper.buildBook
import com.mercadolivro.helper.buildCustomer
import com.mercadolivro.model.BookModel
import com.mercadolivro.repository.BookRepository
import com.mercadolivro.repository.CustomerRepository
import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.*

@ExtendWith(MockKExtension::class, RandomBeansExtension::class)
class BookServiceTest {

    @MockK
    private lateinit var customerRepository: CustomerRepository

    @MockK
    private lateinit var bookRepository: BookRepository

    @InjectMockKs
    @SpyK
    private lateinit var bookService: BookService

    val bookModelSlot = slot<BookModel>()

    val booksModelSlot = slot<List<BookModel>>()

    @Test
    fun `when create should create a book`() {
        val book = buildBook()

        every { bookRepository.save(book) } returns book

        bookService.create(book)

        verify(exactly = 1) { bookRepository.save(book) }
    }

    @Test
    fun `when findAll should return all books`(
        @Random pageable: Pageable,
    ) {
        val expectedBooks = PageImpl(listOf(buildBook(), buildBook()))

        every { bookRepository.findAll(pageable) } returns expectedBooks

        val actualCustomers = bookService.findAll(pageable)

        assertEquals(expectedBooks, actualCustomers)
        verify(exactly = 1) { bookRepository.findAll(pageable) }
    }

    @Test
    fun `when findByActivies should return all active books`(
        @Random pageable: Pageable,
    ) {
        val expectedBooks = PageImpl(listOf(buildBook(), buildBook()))

        every { bookRepository.findByStatus(eq(BookStatus.ATIVO), pageable) } returns expectedBooks

        val actualBooks = bookService.findByActivies(pageable)

        assertEquals(expectedBooks, actualBooks)
        verify(exactly = 1) { bookRepository.findByStatus(eq(BookStatus.ATIVO), pageable) }
    }

    @Test
    fun `when findById should return book when find by id`() {
        val id = 1
        val expectedBook = buildBook(id = id)

        every { bookRepository.findById(id) } returns Optional.of(expectedBook)

        val actualBook = bookService.findById(id)

        assertNotNull(actualBook)
        assertEquals(expectedBook, actualBook)
        verify(exactly = 1) { bookRepository.findById(id) }
    }

    @Test
    fun `when findById should return exception when book not found`() {
        val id = 1

        every { bookRepository.findById(id) } returns Optional.empty()

        val error = assertThrows<NotFoundException> { bookService.findById(id) }

        assertEquals("ML-101", error.errorCode)
        assertEquals("Book [1] not exists", error.message)
        verify(exactly = 1) { bookRepository.findById(id) }
    }

    @Test
    fun `when delete should update book status to canceled`(
        @Random id: Int,
    ) {
        val book = buildBook()
        val expectedBook = book.copy()
        expectedBook.status = BookStatus.CANCELADO

        every { bookService.findById(id) } returns book
        every { bookService.update(expectedBook) } returns expectedBook

        bookService.delete(id)

        verify(exactly = 1) { bookService.findById(id) }
        verify(exactly = 1) { bookService.update(capture(bookModelSlot)) }

        assertTrue(bookModelSlot.isCaptured)
        assertEquals(expectedBook.status, bookModelSlot.captured.status)
    }

    @Test
    fun `when update should update book`() {
        val book = buildBook()

        every { bookRepository.save(book) } returns book

        bookService.update(book)

        verify(exactly = 1) { bookRepository.save(book) }
    }

    @Test
    fun `when deleteByCustomer should delete customer and books`() {
        val customer = buildCustomer()
        val books = listOf(buildBook(), buildBook())

        every { bookRepository.findByCustomer(customer) } returns books
        every { bookRepository.saveAll(books) } returns books

        bookService.deleteByCustomer(customer)

        verify(exactly = 1) { bookRepository.findByCustomer(customer) }
        verify(exactly = 1) { bookRepository.saveAll(capture(booksModelSlot)) }

        assertTrue(booksModelSlot.isCaptured)
        booksModelSlot.captured.map {
            assertEquals(BookStatus.DELETADO, it.status)
        }
    }

    @Test
    fun `when findAllByIds should return books`(
        @Random ids: Set<Int>,
    ) {
        val books = listOf(buildBook(), buildBook())

        every { bookRepository.findAllById(ids) } returns books

        val actualBooks = bookService.findAllByIds(ids)

        assertEquals(books, actualBooks)
        verify(exactly = 1) { bookRepository.findAllById(ids) }
    }
}
