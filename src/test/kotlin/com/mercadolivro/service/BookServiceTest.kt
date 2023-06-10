package com.mercadolivro.service

import com.mercadolivro.enums.BookStatus
import com.mercadolivro.exception.NotFoundException
import com.mercadolivro.helper.buildBook
import com.mercadolivro.repository.BookRepository
import com.mercadolivro.repository.CustomerRepository
import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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
    private lateinit var bookService: BookService

    @Test
    fun `should create a book`() {
        val book = buildBook()

        every { bookRepository.save(book) } returns book

        bookService.create(book)

        verify(exactly = 1) { bookRepository.save(book) }
    }

    @Test
    fun `should return all books`(
        @Random pageable: Pageable,
    ) {
        val expectedBooks = PageImpl(listOf(buildBook(), buildBook()))

        every { bookRepository.findAll(pageable) } returns expectedBooks

        val actualCustomers = bookRepository.findAll(pageable)

        assertEquals(expectedBooks, actualCustomers)
        verify(exactly = 1) { bookRepository.findAll(pageable) }
    }

    @Test
    fun `should return all active books`(
        @Random pageable: Pageable,
    ) {
        val expectedBooks = PageImpl(listOf(buildBook(), buildBook()))

        every { bookRepository.findByStatus(eq(BookStatus.ATIVO), pageable) } returns expectedBooks

        val actualBooks = bookService.findByActivies(pageable)

        assertEquals(expectedBooks, actualBooks)
        verify(exactly = 1) { bookRepository.findByStatus(eq(BookStatus.ATIVO), pageable) }
    }

    @Test
    fun `should return book when find by id`() {
        val id = 1
        val expectedBook = buildBook(id = id)

        every { bookRepository.findById(id) } returns Optional.of(expectedBook)

        val actualBook = bookService.findById(id)

        assertNotNull(actualBook)
        assertEquals(expectedBook, actualBook)
        verify(exactly = 1) { bookRepository.findById(id) }
    }

    @Test
    fun `should return exception when book not found`() {
        val id = 1

        every { bookRepository.findById(id) } returns Optional.empty()

        val error = assertThrows<NotFoundException> { bookService.findById(id) }

        assertEquals("ML-101", error.errorCode)
        assertEquals("Book [1] not exists", error.message)
        verify(exactly = 1) { bookRepository.findById(id) }
    }
}
