package com.mercadolivro.controller

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("admin")
class AdminController() {
    @GetMapping("/reports")
    fun report(): String {
        return "This is a report. Only admin can see it!"
    }
}
