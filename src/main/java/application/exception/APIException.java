package application.exception;

/**
 * API共通例外クラス
 */
public class APIException extends ApplicationException {

    public APIException(HttpError error) {
        super(error);
    }

    public APIException(HttpError error, Throwable cause) {
        super(error, cause);
    }

    public APIException(HttpError error, String... args) {
        super(error, args);
    }

    public APIException(HttpError error, Throwable cause, String... args) {
        super(error, cause, args);
    }
}
