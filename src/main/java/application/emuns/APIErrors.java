package application.emuns;

import org.springframework.http.HttpStatus;

import application.exception.HttpError;

/**
 * APIエラー
 */
public enum APIErrors implements HttpError {

    INVALID_PARAMETER("ERR-998", HttpStatus.BAD_REQUEST, "パラメータが正しくありません。"),
    UNEXPECTED("ERR-999", HttpStatus.INTERNAL_SERVER_ERROR, "予期せぬエラーが発生しました。：{0}");

    private String code;

    private HttpStatus status;

    private String message;

    APIErrors(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    @Override
    public String getErrorCode() {
        return code;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
