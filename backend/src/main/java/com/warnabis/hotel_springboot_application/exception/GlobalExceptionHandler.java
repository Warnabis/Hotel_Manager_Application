package com.warnabis.hotel_springboot_application.exception;

import tools.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
      ResourceNotFoundException ex,
      HttpServletRequest request) {
        log.warn("Ресурс не найден: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
      BadRequestException ex,
      HttpServletRequest request) {
        log.warn("Некорректный запрос: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
      ConflictException ex,
      HttpServletRequest request) {
        log.warn("Конфликт данных: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
      MethodArgumentNotValidException ex,
      HttpServletRequest request) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
          .collect(Collectors.toMap(
            error -> error.getField(),
            error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Некорректное значение",
            (first, second) -> first,
            LinkedHashMap::new
          ));

        log.warn("Ошибка валидации: {}", errors);
        return buildResponse(
          HttpStatus.BAD_REQUEST,
          "Ошибка валидации данных",
          request,
          errors
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
      ConstraintViolationException ex,
      HttpServletRequest request) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
          .collect(Collectors.toMap(
            violation -> violation.getPropertyPath().toString(),
            violation -> violation.getMessage(),
            (first, second) -> first,
            LinkedHashMap::new
          ));

        log.warn("Ошибка валидации параметров: {}", errors);
        return buildResponse(
          HttpStatus.BAD_REQUEST,
          "Ошибка валидации параметров",
          request,
          errors
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpServletRequest request) {
        String message = "Некорректный формат JSON в теле запроса";
        Throwable cause = ex.getRootCause();

        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            String fieldName = "неизвестное поле";
            if (!ife.getPath().isEmpty()) {
                fieldName = ife.getPath().get(0).toString();
                if (fieldName.contains(".")) {
                    fieldName = fieldName.substring(fieldName.lastIndexOf(".") + 1);
                }
            }
            String targetType = ife.getTargetType() != null ? ife.getTargetType().getSimpleName() : "неизвестный";
            message = String.format(
              "Некорректный формат поля '%s'. Ожидался тип: %s",
              fieldName,
              targetType
            );
        }

        log.warn("Ошибка чтения JSON: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, message, request, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex,
      HttpServletRequest request) {
        String name = ex.getName();
        Object value = ex.getValue();
        Class<?> requiredType = ex.getRequiredType();
        String message;

        if (value == null) {
            message = String.format("Параметр '%s' не может быть null", name);
        } else if (requiredType != null) {
            if (ex.getCause() instanceof DateTimeParseException) {
                message = String.format(
                  "Параметр '%s' имеет некорректный формат даты. Ожидаемый формат: yyyy-MM-dd, получено: '%s'",
                  name, value
                );
            } else {
                message = String.format(
                  "Параметр '%s' имеет некорректный тип. Ожидался: %s, получено: '%s'",
                  name, requiredType.getSimpleName(), value
                );
            }
        } else {
            message = String.format("Параметр '%s' имеет некорректное значение: '%s'", name, value);
        }

        log.warn("Ошибка преобразования параметра: {}", message);
        return buildResponse(HttpStatus.BAD_REQUEST, message, request, null);
    }

    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessResourceUsage(
      InvalidDataAccessResourceUsageException ex,
      HttpServletRequest request) {
        String message = "Ошибка при выполнении запроса к базе данных";

        String errorMessage = ex.getMessage();
        if (errorMessage != null) {
            if (errorMessage.contains("could not determine data type")) {
                message = "Некорректный формат параметра. Проверьте правильность введенных данных (например, дата должна быть в формате yyyy-MM-dd)";
            } else if (errorMessage.contains("SQLGrammarException")) {
                message = "Ошибка синтаксиса запроса. Проверьте правильность введенных параметров";
            }
        }

        log.warn("Ошибка выполнения запроса: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, message, request, null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(
      MissingServletRequestParameterException ex,
      HttpServletRequest request) {
        String message = String.format(
          "Отсутствует обязательный параметр '%s' типа %s",
          ex.getParameterName(),
          ex.getParameterType()
        );
        log.warn(message);
        return buildResponse(HttpStatus.BAD_REQUEST, message, request, null);
    }

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<ErrorResponse> handleConversionFailed(
      ConversionFailedException ex,
      HttpServletRequest request) {
        String message = String.format(
          "Невозможно преобразовать значение '%s' к типу %s",
          ex.getValue(),
          ex.getTargetType() != null ? ex.getTargetType().getType().getSimpleName() : "неизвестный"
        );
        log.warn("Ошибка преобразования: {}", message);
        return buildResponse(HttpStatus.BAD_REQUEST, message, request, null);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorResponse> handleDateTimeParseException(
      DateTimeParseException ex,
      HttpServletRequest request) {
        String message = String.format(
          "Некорректный формат даты. Ожидаемый формат: yyyy-MM-dd (пример: 2024-01-15), получено: '%s'",
          ex.getParsedString()
        );
        log.warn("Ошибка парсинга даты: {}", message);
        return buildResponse(HttpStatus.BAD_REQUEST, message, request, null);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<ErrorResponse> handleServletRequestBinding(
      ServletRequestBindingException ex,
      HttpServletRequest request) {
        log.warn("Ошибка привязки запроса: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Некорректный параметр запроса", request, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
      IllegalArgumentException ex,
      HttpServletRequest request) {
        log.warn("Некорректный аргумент: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
      IllegalStateException ex,
      HttpServletRequest request) {
        log.warn("Некорректное состояние: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
      HttpRequestMethodNotSupportedException ex,
      HttpServletRequest request) {
        String message = String.format("Метод %s не поддерживается для этого URL", ex.getMethod());
        log.warn(message);
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, message, request, null);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(
      NoHandlerFoundException ex,
      HttpServletRequest request) {
        String message = String.format("URL %s не найден", ex.getRequestURL());
        log.warn(message);
        return buildResponse(HttpStatus.NOT_FOUND, message, request, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
      AccessDeniedException ex,
      HttpServletRequest request) {
        log.warn("Доступ запрещён: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "Доступ запрещён", request, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
      DataIntegrityViolationException ex,
      HttpServletRequest request) {
        String message = "Операция нарушает ограничения базы данных";
        Throwable cause = ex.getRootCause();

        if (cause instanceof SQLIntegrityConstraintViolationException) {
            String sqlMessage = cause.getMessage();
            if (sqlMessage.contains("foreign key")) {
                message = "Невозможно удалить запись, так как она используется в других таблицах";
            } else if (sqlMessage.contains("unique constraint")) {
                message = "Запись с такими данными уже существует";
            } else {
                message = "Нарушение ограничения целостности данных";
            }
        }

        log.warn("Нарушение целостности данных: {}", ex.getMessage());
        return buildResponse(
          HttpStatus.CONFLICT,
          message,
          request,
          null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
      Exception ex,
      HttpServletRequest request) {
        log.error("Непредвиденная ошибка", ex);

        String message = "Внутренняя ошибка сервера";
        if (ex.getMessage() != null && ex.getMessage().contains("could not determine data type")) {
            message = "Некорректный формат параметра запроса. Проверьте правильность введенных данных";
        }

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, request, null);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
      HttpStatus status,
      String message,
      HttpServletRequest request,
      Map<String, String> validationErrors) {
        ErrorResponse body = ErrorResponse.builder()
          .timestamp(LocalDateTime.now())
          .status(status.value())
          .error(status.getReasonPhrase())
          .message(message)
          .path(request.getRequestURI())
          .validationErrors(validationErrors)
          .build();

        return ResponseEntity.status(status).body(body);
    }
}