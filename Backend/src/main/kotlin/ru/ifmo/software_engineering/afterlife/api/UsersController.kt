package ru.ifmo.software_engineering.afterlife.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.software_engineering.afterlife.security.Principal
import ru.ifmo.software_engineering.afterlife.users.services.AuthorizedUserService

@RestController
@RequestMapping("/api/users")
class UsersController(private val authorizedUserService: AuthorizedUserService) {
    @GetMapping("me")
    suspend fun getCurrentUser(): Principal = authorizedUserService.getCurrentUser()
}