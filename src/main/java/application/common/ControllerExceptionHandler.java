package application.common;

import java.text.MessageFormat;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import application.controller.WebController;
import application.emuns.ApplicationErrors;
import application.exception.ApplicationException;
import application.exception.HttpError;
import lombok.extern.slf4j.Slf4j;

/**
 * 画面コントローラ共通例外処理クラス
 *
 */
@Slf4j
@ControllerAdvice(basePackageClasses = WebController.class)
public class ControllerExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleApplicationException(ApplicationException ex) {
        return handleError(ex.getError(), ex, ex.getArgs());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleRuntimeException(RuntimeException ex) {
        return handleError(ApplicationErrors.UNEXPECTED, ex, ex.toString());
    }

    private ModelAndView handleError(HttpError error, Exception ex, Object... args) {
        String message;
        if (args != null) {
            message = MessageFormat.format(error.getMessage(), args);
        } else {
            message = error.getMessage();
        }

        log.error(message, ex);

        return new ModelAndView("error/app-error")
                .addObject("errorCode", error.getErrorCode())
                .addObject("errorMessage", message);
    }
}
