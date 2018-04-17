package application.exception;

import java.text.MessageFormat;

/**
 * 画面共通例外クラス
 *
 */
public class ApplicationException extends RuntimeException {

    private HttpError error;

    private Throwable cause;

    private Object[] args;

    public ApplicationException(HttpError error) {
        super();
        this.error = error;
    }

    public ApplicationException(HttpError error, Throwable cause) {

        super();
        this.error = error;
        this.cause = cause;
    }

    public ApplicationException(HttpError error, String... args) {
        super();
        this.error = error;
        this.args = args;
    }

    public ApplicationException(HttpError error, Throwable cause, String... args) {
        super();
        this.error = error;
        this.args = args;
        this.cause = cause;
    }

    public String getMessage() {
        if (args != null) {
            return MessageFormat.format(error.getMessage(), args);
        } else {
            return error.getMessage();
        }
    }

    public HttpError getError() {
        return error;
    }

    public Throwable getCause() {
        return cause;
    }

    public Object[] getArgs() {
        return args;
    }
}
