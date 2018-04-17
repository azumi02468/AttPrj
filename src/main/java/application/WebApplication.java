package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Webアプリケーションクラス。
 */
@SpringBootApplication
public class WebApplication {

    /**
     * Webアプリケーションのエントリポイント。
     * @param args 起動パラメータ
     * @throws Exception 未捕捉の例外が発生した場合
     */
    public static void main(final String[] args) throws Exception {
        SpringApplication.run(WebApplication.class, args);
    }
}
