package application.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.oltu.oauth2.common.utils.JSONUtils;

import com.linecorp.bot.model.profile.UserProfileResponse;

/**
 * LINE操作のユーティリティ。
 */
public class LineUtils {

    /**
     * 非公開のコンストラクタ。
     */
    private LineUtils() {
    }

    /**
     * ユーザプロフィールを取得する。<br>
     * @param accessToken ログイン後のaccessToken
     * @return LINEのレスポンス
     */
    public static UserProfileResponse getProfileByAccessToken(String accessToken) {
        String url = "https://api.line.me/v2/profile";
        List<NameValuePair> headers = new ArrayList<>();
        headers.add(new BasicNameValuePair("Authorization", "Bearer " + accessToken));
        String json = getHttp(url, headers);
        Map<String, Object> profileMap = JSONUtils.parseJSON(json);
        UserProfileResponse profile = new UserProfileResponse(
                (String) profileMap.get("displayName"),
                (String) profileMap.get("userId"),
                (String) profileMap.get("pictureUrl"),
                (String) profileMap.get("statusMessage"));
        //        LOG.debug("LINE profile=[" + profile + "]");
        return profile;
    }

    /**
     * GETリクエストを発行する。
     * @param toUrl 送信先
     * @param headers ヘッダーキー値
     * @return レスポンス
     */
    public static String getHttp(String toUrl, List<NameValuePair> headers) {
        HttpURLConnection con = null;
        StringBuilder res = new StringBuilder();
        try {
            URL url = new URL(toUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            // ヘッダー設定
            for (NameValuePair pair : headers) {
                con.setRequestProperty(pair.getName(), pair.getValue());
            }
            con.connect();
            // HTTPレスポンスコード
            final int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                try (InputStream in = con.getInputStream()) {
                    String encoding = con.getContentEncoding();
                    if (StringUtils.isEmpty(encoding)) {
                        encoding = "UTF-8";
                    }
                    try (BufferedReader bufReader = new BufferedReader(new InputStreamReader(in, encoding))) {
                        // 1行ずつテキストを読み込む
                        for (String line = bufReader.readLine(); line != null; line = bufReader.readLine()) {
                            res.append(line);
                        }
                        bufReader.close();
                    }
                }
            } else {
                //                LOG.error("getHttp() {}", status);
                return null;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (con != null) {
                // コネクションを切断
                con.disconnect();
            }
        }
        //        LOG.debug("getHttp() {}", res.toString());
        return res.toString();
    }
}
