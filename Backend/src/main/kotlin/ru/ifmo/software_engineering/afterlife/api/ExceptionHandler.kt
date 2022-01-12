package ru.ifmo.software_engineering.afterlife.api

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import ru.ifmo.software_engineering.afterlife.api.models.ErrorResponse
import ru.ifmo.software_engineering.afterlife.core.exceptions.*

@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {
    companion object {
        val LOG = LoggerFactory.getLogger(ExceptionHandler::class.java)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        if (ex is ApplicationException) {
            return ex.toResponse()
        } else {
            LOG.error("Unhandled exception", ex)
            return ResponseEntity
                    .internalServerError()
                    .body(ErrorResponse("Unexpected error", "500"))
        }
    }
}

fun ApplicationException.toResponse(): ResponseEntity<ErrorResponse> {
    return when (this) {
        is ForbiddenException -> ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse(this.message ?: "Forbidden", "403"))
        is BadRequestException -> ResponseEntity
                .badRequest()
                .body(ErrorResponse(this.message ?: "Bad Request", "400"))
        is NotFoundException -> ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse(this.message ?: "Not Found", "404"))
        is UnauthorizedException -> ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse(this.message ?: "Unauthorized", "401"))
        is ConflictException -> ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse(this.message ?: "Conflict", "409"))
    }
}