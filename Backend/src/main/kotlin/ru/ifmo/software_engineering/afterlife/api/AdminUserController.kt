package ru.ifmo.software_engineering.afterlife.api

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.software_engineering.afterlife.security.Principal
import ru.ifmo.software_engineering.afterlife.users.domain.CreateUserRequest
import ru.ifmo.software_engineering.afterlife.users.services.UsersAdminService

@RestController
@RequestMapping("/api/admin/users")
class AdminUserController(
        private val usersAdminService: UsersAdminService
) {
    @PostMapping
    suspend fun createUser(@RequestBody createUserRequest: CreateUserRequest): Principal {
        return usersAdminService.createUser(createUserRequest)
    }
}