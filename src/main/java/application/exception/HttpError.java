package application.exception;

import org.springframework.http.HttpStatus;

/**
 * HTTPに関するエラーを示すインターフェース。
 */
public interface HttpError {
    /**
     * エラーコードを取得する。
     */
    String getErrorCode();

    /**
     * HTTPスタースを取得する。
     *
     * @return HTTPステータス
     */
    HttpStatus getStatus();

    /**
     * エラーメッセージを取得する。
     *
     * @return エラーメッセージ
     */
    String getMessage();

}
