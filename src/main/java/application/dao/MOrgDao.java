package application.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import application.entity.MOrg;
import ninja.cero.sqltemplate.core.SqlTemplate;

/**
 * 組織マスタDAO。
 */
@Component
public class MOrgDao extends AbstractDao<MOrg> {

    @Autowired
    private SqlTemplate sqlTemplate;

    /**
     * 組織名から組織情報を検索する。
     * @return エンティティリスト
     */
    public List<MOrg> findOrgs(String name) {

        Map<String, Object> cond = new HashMap<>();

        if (!StringUtils.isEmpty(name)) {
            cond.put("likeName", "%" + name + "%");
        }

        return sqlTemplate.forList("sql/MOrgDao/findOrgs.sql", MOrg.class, cond);
    }

    /**
     * 組織コードから組織情報を検索する。
     * @param orgCd 組織コード
     * @return エンティティリスト
     */
    public MOrg findByOrgCd(String orgCd) {
        return sqlTemplate.forObject("sql/MOrgDao/findByOrgCd.sql", MOrg.class, orgCd);
    }

    /**
     * 組織を登録する。
     * @param entity エンティティ
     */
    public int insert(MOrg entity) {
    	setInsertColumns(entity);
        return sqlTemplate.update("sql/MOrgDao/insert.sql", entity);
    }

    /**
     * 組織を更新する。
     * @param entity エンティティ
     */
    public int update(MOrg entity) {
    	setUpdateColumns(entity);
        return sqlTemplate.update("sql/MOrgDao/update.sql", entity);
    }
}
