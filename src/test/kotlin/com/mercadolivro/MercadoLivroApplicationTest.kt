package com.mercadolivro

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.boot.SpringApplication
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MercadoLivroApplicationTest {

    @Test
    fun contextLoads() {
    }

    @Test
    fun `should execute main application`() {
        mockkStatic(SpringApplication::class)
        every { SpringApplication.run(MercadoLivroApplication::class.java, *anyVararg()) } returns mockk()

        main(arrayOf())

        verify { SpringApplication.run(MercadoLivroApplication::class.java, *anyVararg()) }
    }
}
