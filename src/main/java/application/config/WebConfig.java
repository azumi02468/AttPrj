package application.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import application.context.AppMesssageSource;

/**
 * <pre>
 * WEBアプリケーション設定クラス
 * </pre>
 *
 * @author 作成者氏名
 *
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    /**
     * <pre>
     * バリデータの設定.
     * </pre>
     *
     * @return LocalValidatorFactoryBean
     */
    @Bean(name = "validator")
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setValidationMessageSource(messageSource());
        return localValidatorFactoryBean;
    }

    /**
     * <pre>メッセージをUTF-8で管理する設定.</pre>
     *
     * @return MessageSource
     */
    @Bean(name = "messageSource")
    public MessageSource messageSource() {
        // ReloadableResourceBundleMessageSource bean = new ReloadableResourceBundleMessageSource();
        AppMesssageSource bean = new AppMesssageSource();
        bean.setBasename("classpath:Messages_jp");
        bean.setDefaultEncoding("UTF-8");
        return bean;
    }

}
