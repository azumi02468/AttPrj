package application.dao;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.entity.TLineStatus;
import ninja.cero.sqltemplate.core.SqlTemplate;

/**
 * LINEステータス情報DAO。
 * @author 作成者氏名
 */
@Component
public class TLineStatusDao extends AbstractDao<TLineStatus> {

    //    /** このクラスのロガー。 */
    //    private static final Logger logger = LoggerFactory.getLogger(TLineStatusDao.class);

    /** DB操作用。 */
    @Autowired
    private SqlTemplate sqlTemplate;

    /**
     * PKを条件に1行取得する。
     * @param lineId LINE識別子
     * @return 取得した1行を含むSELECT結果。
     */
    public Optional<TLineStatus> selectByPk(String lineId) {
        return Optional
                .ofNullable(sqlTemplate.forObject("sql/TLineStatusDao/select.sql", TLineStatus.class, lineId));
    }

    /**
     * PKを条件に1行取得する。
     * @param lineId LINE識別子
     * @return 1行,存在しない場合null
     */
    public TLineStatus getByPk(String lineId) {
        Optional<TLineStatus> select = selectByPk(lineId);
        TLineStatus res = null;
        if (select.isPresent()) {
            res = select.get();
        }
        return res;
    }

    /**
     * 1行挿入する。
     * @param entity 挿入する1行
     */
    public int insert(TLineStatus entity) {
        return sqlTemplate.update("sql/TLineStatusDao/insert.sql", entity);
    }

    /**
     * 1行更新する。
     * @param entity 更新する1行
     */
    public int update(TLineStatus entity) {
        return sqlTemplate.update("sql/TLineStatusDao/update.sql", entity);
    }
}
