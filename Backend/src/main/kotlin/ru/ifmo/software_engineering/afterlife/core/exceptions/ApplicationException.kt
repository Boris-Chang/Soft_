package ru.ifmo.software_engineering.afterlife.core.exceptions

sealed class ApplicationException(message: String) :
    Exception("Application exception occurred: $message")

class NotFoundException(message: String? = null) :
    ApplicationException(if (message != null) "Entity not found: $message" else "Entity not found")

class BadRequestException(message: String? = null) :
    ApplicationException(if (message != null) "Bad Request: $message" else "Bad Request")

class ForbiddenException(message: String? = null) :
    ApplicationException(if (message != null) "Forbidden: $message" else "Forbidden")

class UnauthorizedException(message: String? = null) :
        ApplicationException(if (message != null) "Unauthorized: $message" else "Unauthorized")

class ConflictException(message: String? = null) :
        ApplicationException(if (message != null) "Conflict: $message" else "Conflict")