package edu.guvi.dreamhome.Exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgument(IllegalArgumentException ex) {
        // For API requests → return JSON
        if (ex.getMessage().startsWith("API_")) {
            Map<String, Object> body = new HashMap<>();
            body.put("error", ex.getMessage().replace("API_", ""));
            return ResponseEntity.badRequest().body(body);
        }

        // For Thymeleaf → return error page
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("message", ex.getMessage());
        return mv;
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneralException(Exception ex) {
        // For API
        if (ex.getMessage() != null && ex.getMessage().contains("API")) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Something went wrong. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

        // For Thymeleaf views
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("message", "An unexpected error occurred: " + ex.getMessage());
        return mv;
    }

     @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
