package com.knowva.app.exceptions

// Custom Exceptions
class ValidationException(message: String, val details: String? = null) : Exception(message)
class ConflictException(message: String) : Exception(message)
class UnauthorizedException(message: String) : Exception(message)
class NotFoundException(message: String) : Exception(message)