package application.config;

import java.util.LinkedHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.DelegatingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.ForwardAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * <pre>
 * Spring Security設定クラス.
 * </pre>
 *
 * @author 作成者氏名
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * <pre>
     * セキュリティ設定を無視するリクエスト設定.
     * 静的リソース(images、css、javascript)に対するアクセスはセキュリティ設定を無視する
     * </pre>
     */
    @Override
    public final void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/img/**",
                "/css/**",
                "/js/**",
                "/lib/**",
                "/user/**",
                "/api/**",
                "/callback",
                "/api_test");
    }

    /**
     * <pre>
     * 認可の設定.
     * </pre>
     *
     * @param HttpSecurity
     * @exception Exception
     */
    @Override
    protected final void configure(final HttpSecurity http) throws Exception {
        // 認可の設定
        http.authorizeRequests().antMatchers("/admin/login", "/admin/login-error", "/admin/login-impossible").permitAll().anyRequest()
                .authenticated() // 全て認証無しの場合アクセス不許可
                .and()
                // ログイン設定
                .formLogin().loginPage("/admin/login") // ログインフォームのパス
                .loginProcessingUrl("/admin/login") // 認証処理のパス
                .defaultSuccessUrl("/admin/user-org") // 認証成功時の遷移先
                .failureHandler(getDelegatingAuthenticationFailureHandler()) // 認証失敗時の遷移先
                .usernameParameter("userId").passwordParameter("password") // ユーザー名、パスワードのパラメータ名
                .and()
                // ログアウト設定
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/admin/logout")) // ログアウト処理のパス
                .logoutSuccessUrl("/admin/login") // ログアウト完了時の遷移先
                .invalidateHttpSession(true) // ログアウト時にセッションを破棄
                .permitAll().and().csrf().disable();
    }

    /**
     * ログイン失敗時のハンドラを返す。
     * @return ログイン失敗時のハンドラ
     */
    private DelegatingAuthenticationFailureHandler getDelegatingAuthenticationFailureHandler() {
    	LinkedHashMap<Class<? extends AuthenticationException>, AuthenticationFailureHandler> map = new LinkedHashMap<>();
    	map.put(AuthenticationServiceException.class, new ForwardAuthenticationFailureHandler("/admin/login-impossible"));
    	return new DelegatingAuthenticationFailureHandler(map, new SimpleUrlAuthenticationFailureHandler("/admin/login-error"));
    }

    /**
     * <pre>
     * パスワードエンコーダーを取得.
     * </pre>
     *
     * @return PasswordEncoder パスワードエンコーダー
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
