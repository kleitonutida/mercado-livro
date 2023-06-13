package com.mercadolivro.events.listener

import com.mercadolivro.events.PurchaseEvent
import com.mercadolivro.helper.buildBook
import com.mercadolivro.helper.buildPurchase
import com.mercadolivro.service.BookService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class UpdateSoldBookListenerTest {

    @MockK
    private lateinit var bookService: BookService

    @InjectMockKs
    private lateinit var updateSoldBookListener: UpdateSoldBookListener

    @Test
    fun `when listen should update status book to sold`() {
        val books = mutableListOf(buildBook())
        val expectedPurchase = buildPurchase(books = books)

        every { bookService.purchase(books) } just runs

        updateSoldBookListener.listen(PurchaseEvent(this, expectedPurchase))

        verify(exactly = 1) { bookService.purchase(books) }
    }
}
