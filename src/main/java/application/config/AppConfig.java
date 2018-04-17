package application.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

import ninja.cero.sqltemplate.core.SqlTemplate;
import ninja.cero.sqltemplate.core.mapper.MapperBuilder;
import ninja.cero.sqltemplate.core.parameter.ParamBuilder;
import ninja.cero.sqltemplate.core.template.TemplateEngine;

/**
 * <pre>
 * アプリケーション設定クラス.
 * </pre>
 *
 * @author 作成者氏名
 *
 */
@Configuration
public class AppConfig {

    /**
     * <pre>
     * Spring BootでSqlTemplateを使うための設定.
     * </pre>
     *
     * @param jdbcTemplate
     * @param namedParameterJdbcTemplate
     * @return SqlTemplate
     */
    @Bean
    public SqlTemplate sqlTemplate(final JdbcTemplate jdbcTemplate,
            final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new SqlTemplate(
                jdbcTemplate,
                namedParameterJdbcTemplate,
                TemplateEngine.FREEMARKER,
                new ParamBuilder(),
                new MapperBuilder());
    }

    /**
     * <pre>
     * Spring BootでJava8 から追加された日時APIを使用するための設定.
     * </pre>
     *
     * @return Java8TimeDialect
     */
    @Bean
    public Java8TimeDialect java8TimeDialect() {
        return new Java8TimeDialect();
    }

    /**
     * <pre>
     * Spring BootでModelMapperを使うための設定.
     * </pre>
     *
     * @return ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
