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

    val bookSlotDelete = slot<BookModel>()

    val booksSlotDeleteByCustomer = slot<List<BookModel>>()

    val bookSlotPurchase = slot<List<BookModel>>()

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
        verify(exactly = 1) { bookService.update(capture(bookSlotDelete)) }

        assertTrue(bookSlotDelete.isCaptured)
        assertEquals(expectedBook.status, bookSlotDelete.captured.status)
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
        verify(exactly = 1) { bookRepository.saveAll(capture(booksSlotDeleteByCustomer)) }

        assertTrue(booksSlotDeleteByCustomer.isCaptured)
        booksSlotDeleteByCustomer.captured.map {
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

    @Test
    fun `when purchase should update book status to sold`() {
        val books = mutableListOf(buildBook(), buildBook())
        val expectedBooks = books.map {
            val book = it.copy()
            book.status = BookStatus.VENDIDO
            book
        }.toCollection(mutableListOf())

        every { bookRepository.saveAll(expectedBooks) } returns expectedBooks

        bookService.purchase(books)

        verify(exactly = 1) { bookRepository.saveAll(capture(bookSlotPurchase)) }

        assertTrue(bookSlotPurchase.isCaptured)
        bookSlotPurchase.captured.map {
            assertEquals(BookStatus.VENDIDO, it.status)
        }
    }

    @Test
    fun `when booksAvaiable should return true`(
        @Random ids: Set<Int>,
    ) {
        val books = listOf(buildBook(), buildBook())

        every { bookService.findAllByIds(ids) } returns books

        val available = bookService.booksAvailable(ids)

        assertTrue(available)
    }

    @Test
    fun `when booksAvaiable should return false`(
        @Random ids: Set<Int>,
    ) {
        val books = listOf(buildBook(status = BookStatus.VENDIDO), buildBook(status = BookStatus.CANCELADO))

        every { bookService.findAllByIds(ids) } returns books

        val available = bookService.booksAvailable(ids)

        assertFalse(available)
    }

    @Test
    fun `when findByCustomer should return books`(
        @Random id: Int,
        @Random pageable: Pageable,
    ) {
        val customer = buildCustomer()
        val expectedBooks = PageImpl(listOf(buildBook(), buildBook()))

        every { customerRepository.findById(id) } returns Optional.of(customer)
        every { bookRepository.findByCustomer(customer, pageable) } returns expectedBooks

        val actualBooks = bookService.findByCustomerId(id, pageable)

        assertEquals(expectedBooks, actualBooks)
        verify(exactly = 1) { customerRepository.findById(id) }
        verify(exactly = 1) { bookRepository.findByCustomer(customer, pageable) }
    }

    @Test
    fun `when findByCustomer should return customer not found`(
        @Random pageable: Pageable,
        @Random id: Int,
    ) {
        val customer = buildCustomer()

        every { customerRepository.findById(id) } returns Optional.empty()

        val error = assertThrows<NotFoundException> { bookService.findByCustomerId(id, pageable) }

        verify(exactly = 1) { customerRepository.findById(id) }
        verify(exactly = 0) { bookRepository.findByCustomer(customer, pageable) }

        assertEquals("ML-201", error.errorCode)
        assertEquals("Customer [$id] not exists", error.message)
    }

    @Test
    fun `when findByCustomerIdAndBookStatus should return books`(
        @Random pageable: Pageable,
        @Random bookStatus: BookStatus,
        @Random id: Int,
    ) {
        val customer = buildCustomer()
        val expectedBooks = PageImpl(listOf(buildBook(), buildBook()))

        every { customerRepository.findById(id) } returns Optional.of(customer)
        every { bookRepository.findByCustomerAndStatus(customer, bookStatus, pageable) } returns expectedBooks

        val actualBooks = bookService.findByCustomerIdAndBookStatus(pageable, id, bookStatus)

        assertEquals(expectedBooks, actualBooks)
        verify(exactly = 1) { customerRepository.findById(id) }
        verify(exactly = 1) { bookRepository.findByCustomerAndStatus(customer, bookStatus, pageable) }
    }

    @Test
    fun `when findByCustomerIdAndBookStatus should return customer not found`(
        @Random pageable: Pageable,
        @Random bookStatus: BookStatus,
        @Random id: Int,
    ) {
        val customer = buildCustomer()

        every { customerRepository.findById(id) } returns Optional.empty()

        val error =
            assertThrows<NotFoundException> { bookService.findByCustomerIdAndBookStatus(pageable, id, bookStatus) }

        verify(exactly = 1) { customerRepository.findById(id) }
        verify(exactly = 0) { bookRepository.findByCustomerAndStatus(customer, bookStatus, pageable) }

        assertEquals("ML-201", error.errorCode)
        assertEquals("Customer [$id] not exists", error.message)
    }
}
