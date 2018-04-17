package application.webapi.line;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * LINE v2 API interface.
 */
public interface LineAPI {

    /**
     * アクセストークンを発行する。
     * @param grant_type 付与タイプ
     * @param client_id チャネルID
     * @param client_secret チャネルシークレット
     * @param callback_url コールバックURL
     * @param code 認可コード
     * @return アクセストークン
     */
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("oauth2/v2.1/token")
    Call<AccessToken> accessToken(
            @Field("grant_type") String grant_type,
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("redirect_uri") String callback_url,
            @Field("code") String code);

    /**
     * アクセストークンを更新する。
     * @param grant_type "refresh_token"固定
     * @param refresh_token リフレッシュトークン
     * @param client_id チャネルID
     * @param client_secret チャネルシークレット
     * @return アクセストークン
     */
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("oauth2/v2.1/token")
    Call<AccessToken> refreshToken(
            @Field("grant_type") String grant_type,
            @Field("refresh_token") String refresh_token,
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret);

    /**
     * アクセストークンを検証する。
     * @param access_token アクセストークン
     * @return 検証結果
     */
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @GET("oauth2/v2.1/verify")
    Call<Verify> verify(
            @Query("access_token") String access_token);

    /**
     * アクセストークンを取り消す。
     * @param access_token アクセストークン
     * @param client_id チャネルID
     * @param client_secret チャネルシークレット
     * @return 空。値は無し。
     */
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("oauth2/v2.1/revoke")
    Call<Void> revoke(
            @Field("access_token") String access_token,
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret);

}
