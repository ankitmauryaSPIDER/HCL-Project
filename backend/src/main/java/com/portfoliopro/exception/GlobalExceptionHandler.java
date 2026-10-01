package com.portfoliopro.exception;
import org.springframework.http.*; import org.springframework.security.authentication.BadCredentialsException; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import java.util.Map;
@RestControllerAdvice public class GlobalExceptionHandler {
 @ExceptionHandler({IllegalArgumentException.class,BadCredentialsException.class}) @ResponseStatus(HttpStatus.BAD_REQUEST) public Map<String,String> bad(Exception e){return Map.of("message",e.getMessage()==null?"Invalid request":e.getMessage());}
 @ExceptionHandler(MethodArgumentNotValidException.class) @ResponseStatus(HttpStatus.BAD_REQUEST) public Map<String,String> validation(MethodArgumentNotValidException e){String m=e.getBindingResult().getFieldErrors().stream().findFirst().map(x->x.getField()+": "+x.getDefaultMessage()).orElse("Validation failed");return Map.of("message",m);}
}
