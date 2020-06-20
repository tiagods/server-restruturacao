package com.tiagods.prolink.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<?> clientNotFoundException(ClientNotFoundException ex, HttpServletResponse response) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, Arrays.asList(ex.getMessage()));
    }

    @ExceptionHandler(InvalidNickException.class)
    public ResponseEntity<?> invalidNickException(InvalidNickException ex, HttpServletResponse response) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, Arrays.asList(ex.getMessage()));
    }

    @ExceptionHandler(ObrigacaoNotFoundException.class)
    public ResponseEntity<?> obrigacaoNotFoundException(ObrigacaoNotFoundException ex, HttpServletResponse response) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, Arrays.asList(ex.getMessage()));
    }

    @ExceptionHandler(ParametroIncorretoException.class)
    public ResponseEntity<?> parametroIncorretoException(ParametroIncorretoException ex, HttpServletResponse response) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, Arrays.asList(ex.getMessage()));
    }

    @ExceptionHandler(ParametroNotFoundException.class)
    public ResponseEntity<?> parametroNotFoundException(ParametroNotFoundException ex, HttpServletResponse response) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, Arrays.asList(ex.getMessage()));
    }

    @ExceptionHandler(PathInvalidException.class)
    public ResponseEntity<?> pathInvalidException(PathInvalidException ex, HttpServletResponse response) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, Arrays.asList(ex.getMessage()));
    }

    @ExceptionHandler(EstruturaNotFoundException.class)
    public ResponseEntity<?> estruturaNotFoundException(EstruturaNotFoundException ex, HttpServletResponse response) {
        return buildResponseEntity(HttpStatus.NOT_FOUND, Arrays.asList(ex.getMessage()));
    }

    @ExceptionHandler(FolderCuncurrencyJob.class)
    public ResponseEntity<?> folderCuncurrencyJob(FolderCuncurrencyJob ex, HttpServletResponse response) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, Arrays.asList(ex.getMessage()));
    }

    private ResponseEntity<?> buildResponseEntity(HttpStatus status, List<String> erros) {
        ResultError resultError = new ResultError(new Date(),status.value(), erros);
        return new ResponseEntity<>(resultError, status);
    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        ResultError error = new ResultError();
        error.setTimestamp(new Date());
        error.setStatus(status.value());
        error.setErrors(ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList()));
        return new ResponseEntity<>(error, headers, status);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class ResultError{
        private Date timestamp;
        private int status;
        private List<String> errors;
    }
}
