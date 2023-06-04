package com.mercadolivro.repository

import com.mercadolivro.helper.buildCustomer
import io.github.glytching.junit.extension.random.Random
import io.github.glytching.junit.extension.random.RandomBeansExtension
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockKExtension::class, RandomBeansExtension::class)
class CustomerRepositoryTest {

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @BeforeEach
    fun setup() = customerRepository.deleteAll()

    @Test
    fun `should return name containing`(
        @Random pageable: Pageable,
    ) {
        val page = Pageable.ofSize(10).withPage(0)
        val marcos = customerRepository.save(buildCustomer(name = "Marcos"))
        val matheus = customerRepository.save(buildCustomer(name = "Matheus"))
        customerRepository.save(buildCustomer(name = "Alex"))

        val customers = customerRepository.findByNameContaining(page, "Ma")

        assertEquals(listOf(marcos, matheus), customers.content)
    }

    @Nested
    inner class `exists by email` {
        @Test
        fun `should return true when email exists`() {
            val email = "email@teste.com"
            customerRepository.save(buildCustomer(email = email))

            val exists = customerRepository.existsByEmail(email)

            assertTrue(exists)
        }

        @Test
        fun `should return false when email does not exists`() {
            val email = "invalidemail@teste.com"

            val exists = customerRepository.existsByEmail(email)

            assertFalse(exists)
        }
    }

    @Nested
    inner class `find by email` {
        @Test
        fun `should return customer when email exists`() {
            val email = "email@teste.com"
            val expectedCustomer = customerRepository.save(buildCustomer(email = email))

            val customer = customerRepository.findByEmail(email)

            assertNotNull(customer)
            assertEquals(expectedCustomer, customer)
        }

        @Test
        fun `should return null when email does not exists`() {
            val email = "invalidemail@teste.com"

            val customer = customerRepository.findByEmail(email)

            assertNull(customer)
        }
    }
}
