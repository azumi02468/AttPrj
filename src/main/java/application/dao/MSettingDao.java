package application.dao;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.entity.MSetting;
import lombok.extern.slf4j.Slf4j;
import ninja.cero.sqltemplate.core.SqlTemplate;

/**
 * 設定マスタDAO。
 * @author 作成者氏名
 */
@Slf4j
@Component
public class MSettingDao extends AbstractDao<MSetting> {

    @Autowired
    private SqlTemplate sqlTemplate;

    /**
     * 設定マスタ情報を取得する
     *
     * @return 設定マスタエンティティ
     */
    public Optional<MSetting> select() {
        return Optional.ofNullable(sqlTemplate.forObject("sql/MSettingDao/select.sql", MSetting.class));
    }

    /**
     * 設定マスタ情報を取得する。
     *
     * @return 設定マスタエンティティ
     */
    public MSetting get() {
        Optional<MSetting> option = select();
        MSetting res = null;
        if (option.isPresent()) {
            res = option.get();
        }
        return res;
    }

    /**
     * 設定マスタ情報を追加する
     *
     * @param entity
     *            設定マスタエンティティ
     * @return 更新件数
     */
    public int insert(MSetting entity) {
        return sqlTemplate.update("sql/MSettingDao/insert.sql", entity);
    }

    /**
     * 設定マスタ情報を更新する
     *
     * @param entity
     *            設定マスタエンティティ
     * @return 更新件数
     */
    public int update(MSetting entity) {
        log.debug("updateDate:{}", entity.getUpdateDate());
        return sqlTemplate.update("sql/MSettingDao/update.sql", entity);
    }
}
