package com.milosavljevic.lazar.holycode;

import com.milosavljevic.lazar.holycode.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorMapper {

  @ExceptionHandler({NotFoundException.class})
  public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<ErrorResponse> handleDefault(Exception ex) {
    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(ex.getMessage()));
  }


  @AllArgsConstructor
  @Getter
  private static class ErrorResponse {
    private String message;
  }
}
