package com.mercadolivro.service

import com.mercadolivro.enums.CustomerStatus
import com.mercadolivro.enums.Role
import com.mercadolivro.exception.NotFoundException
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
import org.junit.jupiter.api.assertThrows
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

    @Test
    fun `should create customer and encrypt password`(
        @Random password: String,
        @Random encryptPassword: String,
    ) {
        val customer = buildCustomer(password = password)
        val encryptedCustomer = customer.copy(password = encryptPassword)

        every { customerRepository.save(encryptedCustomer) } returns customer
        every { bCrypt.encode(password) } returns encryptPassword

        customerService.create(customer)

        verify(exactly = 1) { customerRepository.save(encryptedCustomer) }
        verify(exactly = 1) { bCrypt.encode(password) }
    }

    @Test
    fun `should return customer by id`(
        @Random id: Int,
    ) {
        val expectedCustomer = buildCustomer(id = id)

        every { customerRepository.findById(id) } returns Optional.of(expectedCustomer)

        val customer = customerService.findById(id)

        assertEquals(expectedCustomer, customer)
        verify(exactly = 1) { customerRepository.findById(id) }
    }

    @Test
    fun `should throw error when customer not found`(
        @Random id: Int,
    ) {
        every { customerRepository.findById(id) } returns Optional.empty()

        val error = assertThrows<NotFoundException> { customerService.findById(id) }

        assertEquals("Customer [$id] not exists", error.message)
        assertEquals("ML-201", error.errorCode)
        verify(exactly = 1) { customerRepository.findById(id) }
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
