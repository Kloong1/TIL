package hello.exception.exceptionhandler.advice;

import hello.exception.exception.UserException;
import hello.exception.exceptionhandler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RestControllerAdvice("hello.exception.api")
public class ExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalArgExHandler(IllegalArgumentException e) {
        log.error("[illegalArgExHandler] ex", e);
        return new ErrorResult("Bad", e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandler(UserException e) {
        log.info("[userExHandler] ex", e);
        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandler(Exception e) {
        log.info("[exHandler] ex", e);
        return new ErrorResult("EX", "내부 오류");
    }

    //@RestController 에서도 ModelAndView 를 반환하면
    //ViewResolver가 동작해서 화면을 렌더링해서 보여준다
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ModelAndView modelAndViewExHandler(ArrayIndexOutOfBoundsException e) {
        log.info("[modelAndViewExHandler] ex", e);
        return new ModelAndView("/error/4xx");
    }
}
