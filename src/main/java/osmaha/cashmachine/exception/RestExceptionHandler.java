package osmaha.cashmachine.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import osmaha.cashmachine.dto.response.MessageResponseDTO;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity handleResourceNotFound(ResourceNotFoundException e){
        log.warn("IN handleResourceNotFound - resource not found: {}", e.getMessage());
        return new ResponseEntity<>(new MessageResponseDTO(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class, OperationException.class})
    protected ResponseEntity handleIllegalState(Exception e){
        log.warn("IN handleIllegalState - illegal argument or state: {}", e.getMessage());
        return new ResponseEntity<>(new MessageResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

}
