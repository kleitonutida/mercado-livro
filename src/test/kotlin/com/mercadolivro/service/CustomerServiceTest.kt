package com.mercadolivro.service

import com.mercadolivro.enums.CustomerStatus
import com.mercadolivro.enums.Role
import com.mercadolivro.model.CustomerModel
import com.mercadolivro.repository.CustomerRepository
import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

@ExtendWith(MockKExtension::class, RandomBeansExtension::class)
class CustomerServiceTest {

    @MockK
    private lateinit var customerRepository: CustomerRepository

    @MockK
    private lateinit var bookService: BookService

    @MockK
    private lateinit var bCrypt: BCryptPasswordEncoder

    @InjectMockKs
    private lateinit var customerService: CustomerService

    @Test
    fun `should return all customers`(
        @Random pageable: Pageable,
    ) {
        val expectedCustomers = PageImpl(listOf(buildCustomer(), buildCustomer()))

        every { customerRepository.findAll(pageable) } returns expectedCustomers

        val actualCustomers = customerService.getAll(pageable, null)

        assertEquals(expectedCustomers, actualCustomers)
        verify(exactly = 1) { customerRepository.findAll(pageable) }
        verify(exactly = 0) { customerRepository.findByNameContaining(any(), any()) }
    }

    @Test
    fun `should return customers when name is informed`(
        @Random pageable: Pageable,
        @Random(size = 20) name: String,
    ) {
        val expectedCustomers = PageImpl(listOf(buildCustomer(), buildCustomer()))

        every { customerRepository.findByNameContaining(pageable, name) } returns expectedCustomers

        val actualCustomers = customerService.getAll(pageable, name)

        assertEquals(expectedCustomers, actualCustomers)
        verify(exactly = 0) { customerRepository.findAll(pageable) }
        verify(exactly = 1) { customerRepository.findByNameContaining(any(), any()) }
    }

    fun buildCustomer(
        id: Int? = null,
        name: String = "customer name",
        email: String = "${UUID.randomUUID()}@email.com",
        password: String = "password",
    ) = CustomerModel(
        id = id,
        name = name,
        email = email,
        status = CustomerStatus.ATIVO,
        password = password,
        roles = setOf(Role.CUSTOMER),
    )
}
