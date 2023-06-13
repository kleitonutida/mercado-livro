package com.mercadolivro.controller

import com.mercadolivro.helper.buildCustomer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
@ActiveProfiles("test")
@WithMockUser
class AdminControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `when report should return message to validate admin`() {
        val customer = buildCustomer()

        mockMvc.perform(get("/admin/reports"))
            .andExpect(content().string("This is a report. Only admin can see it!"))
    }

    @Test
    @WithMockUser(roles = ["CUSTOMER"])
    fun `when report should return message invalid access`() {
        val customer = buildCustomer()

        mockMvc.perform(get("/admin/reports"))
            .andExpect(status().isForbidden)
    }
}
