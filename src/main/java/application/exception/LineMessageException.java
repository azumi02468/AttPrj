package application.exception;

/**
 * LINEにメッセージ通知が必要な例外。
 */
public class LineMessageException extends Exception {
    /** 通知先のLINE識別子。 */
    private String lineId;

    /** リプライToken。 */
    private String repryToken;

    /**
     * 初期値付きコンストラクタ。<br>
     * lineIdとreplyTokenのどちらか一方の値があれば通知できる。
     * @param lineId 通知先LINE識別子
     * @param replyToken リプライToken
     * @param msg
     */
    public LineMessageException(String lineId, String repryToken, String msg) {
        super(msg);
        this.lineId = lineId;
        this.repryToken = repryToken;
    }

    /**
     * 通知先のLINE識別子を取得する。
     * @return 通知先のLINE識別子。
     */
    public String getLineId() {
        return lineId;
    }

    /**
     * リプライTokenを取得する。
     * @return リプライToken
     */
    public String getRepryToken() {
        return repryToken;
    }

}
