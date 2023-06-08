package com.mercadolivro.service

import com.mercadolivro.exception.AuthenticationException
import com.mercadolivro.helper.buildCustomer
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
import java.util.*

@ExtendWith(MockKExtension::class, RandomBeansExtension::class)
class UserDetailCustomServiceTest {

    @MockK
    private lateinit var customerRepository: CustomerRepository

    @InjectMockKs
    private lateinit var userDetailCustomService: UserDetailCustomService

    @Test
    fun `should load user by username when inform valid user id`(
        @Random id: Int,
    ) {
        val customer = buildCustomer(id = id)

        every { customerRepository.findById(id) } returns Optional.of(customer)

        val userDetails = userDetailCustomService.loadUserByUsername(id.toString())

        assertNotNull(userDetails)
        assertEquals(id.toString(), userDetails.username)
        verify(exactly = 1) { customerRepository.findById(id) }
    }

    @Test
    fun `should return erro on load user by username when inform invalid user id`(
        @Random id: Int,
    ) {
        every { customerRepository.findById(id) } returns Optional.empty()

        val error =
            assertThrows<AuthenticationException> { userDetailCustomService.loadUserByUsername(id.toString()) }

        assertEquals("Usuário não encontrado!", error.message)
        assertEquals("999", error.errorCode)
        verify(exactly = 1) { customerRepository.findById(id) }
    }
}
