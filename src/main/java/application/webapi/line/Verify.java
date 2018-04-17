package application.webapi.line;

/**
 * アクセストークンの検証結果を保持する不変クラス。
 */
public final class Verify {
    /** アクセストークンを介して付与される権限。 */
    public final String scope;
    /** アクセストークンを発行する対象のチャネルID。 */
    public final String client_id;
    /** アクセストークンの有効期限。 */
    public final Integer expires_in;

    /**
     * 唯一のコンストラクタ。
     * @param scope 権限
     * @param client_id チャネルID
     * @param expires_in 有効期限
     */
    public Verify(String scope, String client_id, Integer expires_in) {
        this.scope = scope;
        this.client_id = client_id;
        this.expires_in = expires_in;
    }
}
