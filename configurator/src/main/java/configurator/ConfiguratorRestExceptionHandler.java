package configurator;

import configurator.exceptions.ConfiguratorItemAlreadyExistExist;
import configurator.exceptions.ConfiguratorItemNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ConfiguratorRestExceptionHandler {

    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED, reason = "Invalid input")
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void ValidationErrorHandler(){
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Item not found")
    @ExceptionHandler(ConfiguratorItemNotFoundException.class)
    public void ItemNotFoundHandler(){
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Item already exist")
    @ExceptionHandler(ConfiguratorItemAlreadyExistExist.class)
    public void ItemAlredyExistHandler(){
    }

}