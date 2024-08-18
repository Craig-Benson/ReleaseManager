package com.release_manager.exception

import org.springframework.context.MessageSourceResolvable
import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.HandlerMethodValidationException

@RestControllerAdvice
class CustomResponseEntityExceptionHandler

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodValidationErrors(
        exception: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ErrorDetails> {

        val errors = exception.bindingResult.allErrors.map(DefaultMessageSourceResolvable::getDefaultMessage).toList()
        val errorDetails = ErrorDetails("Invalid input", errors)
        return ResponseEntity(errorDetails, HttpHeaders(), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handleMethodValidationErrors(
        exception: HandlerMethodValidationException,
        request: WebRequest
    ): ResponseEntity<ErrorDetails> {

        val errors = exception.allErrors.map(
            MessageSourceResolvable::getDefaultMessage).toList()
        val errorDetails = ErrorDetails("Invalid input", errors)
        return ResponseEntity(errorDetails, HttpHeaders(), HttpStatus.BAD_REQUEST)
    }


