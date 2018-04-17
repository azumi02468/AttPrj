package application.common;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import application.controller.APIController;
import application.emuns.APIErrors;
import application.exception.APIException;
import application.exception.HttpError;

/**
 * Restコントローラ共通例外処理クラス
 *
 */

@ControllerAdvice(basePackageClasses = APIController.class)
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestControllerExceptionHandler.class);

    /**
     * REST APIエラー処理
     *
     * @param request
     *            リクエスト
     * @param ex
     *            例外
     * @return レスポンス
     */
    @ExceptionHandler(value = APIException.class)
    @ResponseBody
    public ResponseEntity<RestError> handleAPIException(HttpServletRequest request, APIException ex) {
        return handleError(ex.getError(), ex);
    }

    /**
     * 実行時エラー処理
     *
     * @param request
     *            リクエスト
     * @param ex
     *            例外
     * @return レスポンス
     */
    @ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    public ResponseEntity<RestError> handleRuntimeException(HttpServletRequest request, RuntimeException ex) {
        return handleError(APIErrors.UNEXPECTED, ex);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers, HttpStatus status, WebRequest request) {
        final Map<String, String> errorDetail = new HashMap<>();
        errorDetail.put("code", APIErrors.UNEXPECTED.getErrorCode());
        errorDetail.put("message", ex.getMessage());
        final RestError restError = new RestError(errorDetail);

        return new ResponseEntity<>(restError, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        final String message = ex.getBindingResult().getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(" / "));

        final Map<String, String> errorDetail = new HashMap<>();
        errorDetail.put("code", APIErrors.INVALID_PARAMETER.getErrorCode());
        errorDetail.put("message", message);
        final RestError restError = new RestError(errorDetail);

        return new ResponseEntity<>(restError, status);
    }

    /**
     * 共通エラー処理
     *
     * @param error
     *            エラー内容
     * @param ex
     *            例外
     * @return レスポンス
     */
    private ResponseEntity<RestError> handleError(HttpError error, Exception ex) {
        final Map<String, String> errorDetail = new HashMap<>();
        errorDetail.put("code", error.getErrorCode());
        errorDetail.put("message", ex.getMessage());
        final RestError restError = new RestError(errorDetail);

        logger.error(error.getMessage(), ex);

        return new ResponseEntity<>(restError, error.getStatus());
    }

    private static class RestError {
        private Map<String, String> error;

        RestError(Map<String, String> error) {
            this.error = error;
        }

        @SuppressWarnings("unused")
        public Map<String, String> getError() {
            return error;
        }

    }
}
