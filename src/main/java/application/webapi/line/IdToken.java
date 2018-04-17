package application.webapi.line;

/**
 * LINE IDトークンが持つ内容を保持する不変クラス。
 */
public final class IdToken {
    public final String iss;
    public final String sub;
    public final String aud;
    public final Long exp;
    public final Long iat;
    public final String nonce;
    public final String name;
    public final String picture;

    /**
     * 唯一のコンストラクタ。
     * @param iss 未使用
     * @param sub 未使用
     * @param aud 未使用
     * @param exp 未使用
     * @param iat 未使用
     * @param nonce 未使用
     * @param name 名前
     * @param picture ユーザイメージURL
     */
    public IdToken(String iss, String sub, String aud, Long exp, Long iat, String nonce, String name, String picture) {
        this.iss = iss;
        this.sub = sub;
        this.aud = aud;
        this.exp = exp;
        this.iat = iat;
        this.nonce = nonce;
        this.name = name;
        this.picture = picture;
    }

    /**
     * 全項目の文字列表現を取得する。
     * @return 全項目の文字列表現
     */
    @Override
    public String toString() {
        return "IdToken [iss=" + iss + ", sub=" + sub + ", aud=" + aud + ", exp=" + exp + ", iat=" + iat + ", nonce="
                + nonce + ", name=" + name + ", picture=" + picture + "]";
    };

}
