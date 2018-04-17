package application.utils;

import java.io.IOException;
import java.util.function.Function;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * HTTPリクエストに関するユーティリティ。
 */
public final class ClientUtils {

    /**
     * HTTPリクエスト発行処理を生成する。
     * @param url 送信先URL
     * @param service サービスクラス
     * @param function 処理関数
     * @return 発行処理
     */
    public static <T, R> R getClient(
            final String url,
            final Class<T> service,
            final Function<T, Call<R>> function) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        T t = retrofit.create(service);
        Call<R> call = function.apply(t);
        try {
            return call.execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
