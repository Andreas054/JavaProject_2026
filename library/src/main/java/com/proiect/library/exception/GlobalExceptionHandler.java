package com.proiect.library.exception;

import com.proiect.library.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Object handleGlobalException(HttpServletRequest request, Exception ex) {
        logger.error("An unexpected error occurred processing request: {}", request.getRequestURL(), ex);

        if (request.getRequestURI().startsWith("/api")) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", "An unexpected error occurred: " + ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFoundException(HttpServletRequest request, ResourceNotFoundException ex) {
        logger.error("Resource not found: {}", ex.getMessage(), ex);
        if (request.getRequestURI().startsWith("/api")) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public Object handleDuplicateResourceException(HttpServletRequest request, DuplicateResourceException ex) {
        logger.error("Duplicate resource: {}", ex.getMessage(), ex);
        if (request.getRequestURI().startsWith("/api")) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(BusinessException.class)
    public Object handleBusinessException(HttpServletRequest request, BusinessException ex) {
        logger.error("Business exception: {}", ex.getMessage(), ex);
        if (request.getRequestURI().startsWith("/api")) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidationExceptions(HttpServletRequest request, MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage(), ex);
        if (request.getRequestURI().startsWith("/api")) {
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", "Validation failed. Please check your inputs.");
        return modelAndView;
    }
}