package application.context;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * メッセージリソースアクセスクラス。
 * @author 作成者氏名
 */
public class AppMesssageSource extends ReloadableResourceBundleMessageSource {

    /** 生成したインスタンス。 */
    private static ReloadableResourceBundleMessageSource instance;

    /**
     * 唯一のコンストラクタ。
     */
    public AppMesssageSource() {
        super();
        instance = this;
    }

    /**
     * メッセージを取得する。
     * @param code メッセージコード
     * @return メッセージ
     */
    public static String getMessage(String code) {
        return instance.getMessage(code, null, null);
    }

    /**
     * メッセージを取得する。
     * @param code メッセージコード
     * @param binds バインド値
     * @return メッセージ
     */
    public static String getMessage(String code, Object... binds) {
        return instance.getMessage(code, binds, null);
    }
}
