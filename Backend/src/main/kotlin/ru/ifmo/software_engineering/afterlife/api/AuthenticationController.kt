package ru.ifmo.software_engineering.afterlife.api

import io.swagger.v3.oas.annotations.security.SecurityRequirements
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.ifmo.software_engineering.afterlife.api.models.AuthenticationRequest
import ru.ifmo.software_engineering.afterlife.api.models.ErrorResponse
import ru.ifmo.software_engineering.afterlife.api.models.TokenResponse
import ru.ifmo.software_engineering.afterlife.security.services.AuthenticationService

@SecurityRequirements
@RestController
@RequestMapping("/api/sign-in")
class AuthenticationController(
        private val authenticationService: AuthenticationService
) {
    @PostMapping
    suspend fun signIn(@RequestBody loginRequest: AuthenticationRequest): ResponseEntity<Any> {
        val token = authenticationService.authenticationToken(loginRequest.login, loginRequest.password)
                ?: return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ErrorResponse("Bad credentials", "401"))

        return ResponseEntity.ok(TokenResponse(token))
    }
}