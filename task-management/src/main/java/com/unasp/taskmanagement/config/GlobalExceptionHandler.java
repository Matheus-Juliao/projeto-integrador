package com.unasp.taskmanagement.config;

import com.unasp.taskmanagement.config.component.MessageProperty;
import com.unasp.taskmanagement.config.dto.ErrorValidationDTO;
import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.exception.BusinessException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @Autowired
  private MessageProperty messageProperty;

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public List<ErrorValidationDTO> handleValidationField(MethodArgumentNotValidException e) {
    return e.getBindingResult()
        .getFieldErrors()
        .stream().map(fieldError -> ErrorValidationDTO.builder()
            .code(HttpStatus.BAD_REQUEST.value())
            .field(fieldError.getField())
            .message(fieldError.getDefaultMessage())
            .build())
        .collect(Collectors.toList());
  }

  @ExceptionHandler({ BusinessException.class,
                      HttpMessageNotReadableException.class })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Object> handleNotValid(RuntimeException e) {
    return new ResponseEntity<>(Messages.builder()
        .code(HttpStatus.BAD_REQUEST.value())
        .message(e.getMessage())
        .build(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({ BadCredentialsException.class,
                      InternalAuthenticationServiceException.class })
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<Object> handleForbidden(RuntimeException e) {
    return new ResponseEntity<>(Messages.builder()
        .code(HttpStatus.FORBIDDEN.value())
        .message(messageProperty.getProperty("error.userOrPasswordInvalid"))
        .build(), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleExceptionGeneral(Exception e){
    log.error("error:", e);
    return new ResponseEntity<>(Messages.builder()
        .message(messageProperty.getProperty("error.internalServerError"))
        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .build(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}