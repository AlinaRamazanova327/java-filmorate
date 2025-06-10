package ru.yandex.practicum.filmorate.advice;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ErrorResponse;
import ru.yandex.practicum.filmorate.dto.Violation;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onConstraintValidationException(ConstraintViolationException e) {
        final List<Violation> violations = e.getConstraintViolations().stream()
                .map(violation -> new Violation(violation.getPropertyPath().toString(), violation.getMessage()))
                .collect(Collectors.toList());
        return new ErrorResponse(violations);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ErrorResponse(violations);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        List<Violation> violations = Collections.singletonList(new Violation("not-found", e.getMessage()));
        return new ErrorResponse(violations);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleRuntimeException(RuntimeException e) {
        List<Violation> violations = Collections.singletonList(new Violation("runtime-error",
                e.getMessage()));
        return new ErrorResponse(violations);
    }
}