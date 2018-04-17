package application.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.entity.MDivDetail;
import ninja.cero.sqltemplate.core.SqlTemplate;

/**
 * 汎用区分（明細）マスタDAO
 *
 * @author 作成者氏名
 *
 */
@Component
public class MDivDao {

    @Autowired
    private SqlTemplate sqlTemplate;

    /**
     * 指定区分IDの明細を取得する。
     *
     * @param divId 区分ID
     * @return 区分明細エンティティリスト
     */
    public List<MDivDetail> findDetailById(int divId) {
        Map<String, Object> cond = new HashMap<>();
        cond.put("divId", divId);
        return sqlTemplate.forList("sql/MDivDao/findDetailById.sql", MDivDetail.class, cond);
    }

    /**
     * 指定区分IDおよびコードの明細を取得する。
     *
     * @param divId 区分ID
     * @param divCd 区分コード
     * @return 区分明細エンティティリスト
     */
    public MDivDetail findDetailByIdAndCd(int divId, String divCd) {
        Map<String, Object> cond = new HashMap<>();
        cond.put("divId", divId);
        cond.put("divCd", divCd);
        return sqlTemplate.forObject("sql/MDivDao/findDetailById.sql", MDivDetail.class, cond);
    }
}
