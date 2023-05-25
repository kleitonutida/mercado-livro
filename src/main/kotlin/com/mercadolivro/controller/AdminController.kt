package com.mercadolivro.controller

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("admins")
class AdminController() {
    @GetMapping("/report")
    fun report(): String {
        return "This is a report. Only admin can see it!"
    }
}
