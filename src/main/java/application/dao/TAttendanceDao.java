package application.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.entity.TAttendance;
import ninja.cero.sqltemplate.core.SqlTemplate;

/**
 * 勤怠情報DAO。
 * @author 作成者氏名
 */
@Component
public class TAttendanceDao extends AbstractDao<TAttendance> {
    //    /** このクラスのロガー。 */
    //    private static final Logger logger = LoggerFactory.getLogger(TAttendanceDao.class);

    /** DB操作用。 */
    @Autowired
    private SqlTemplate sqlTemplate;

    /**
     * PKを条件に1行取得する。
     * @param userId ユーザID
     * @param attendanceCd 勤怠区分コード
     * @param yyyymmdd 出勤日
     * @return 取得した1行を含むSELECT結果。
     */
    public Optional<TAttendance> selectByPk(Integer userId, String attendanceCd, String yyyymmdd) {
        return Optional.ofNullable(sqlTemplate.forObject(
                "sql/TAttendanceDao/selectByPk.sql", TAttendance.class, userId, attendanceCd, yyyymmdd));
    }

    /**
     * PKを条件に1行取得する。
     * @param userId ユーザID
     * @param attendanceCd 勤怠区分コード
     * @param yyyymmdd 出勤日
     * @return 取得した1行,見つからない場合null
     */
    public TAttendance getByPk(Integer userId, String attendanceCd, String yyyymmdd) {
        Optional<TAttendance> option = selectByPk(userId, attendanceCd, yyyymmdd);
        TAttendance res = null;
        if (option.isPresent()) {
            res = option.get();
        }
        return res;
    }

    /**
     * 年月を条件に複数行取得する。
     * @param userId ユーザID
     * @param yyyymm 出勤日(年月)
     * @return SELECT結果
     */
    public List<TAttendance> selectByMonth(Integer userId, String yyyymm) {
        return sqlTemplate.forList("sql/TAttendanceDao/select.sql", TAttendance.class, userId, yyyymm);
    }

    /**
     * 年月を条件に複数行取得する。
     * @param userId ユーザID
     * @param yyyymm 出勤日(年月)
     * @return SELECT結果
     */
    public List<TAttendance> selectByMonth(String yyyymm) {
        return sqlTemplate.forList("sql/TAttendanceDao/selectByAttendanceDay.sql", TAttendance.class, yyyymm + "%");
    }

    /**
     * 1行挿入する。
     * @param entity 挿入する1行
     */
    public int insert(TAttendance entity) {
        return sqlTemplate.update("sql/TAttendanceDao/insert.sql", entity);
    }

    /**
     * 1行更新する。
     * @param entity 更新する1行
     */
    public int update(TAttendance entity) {
        return sqlTemplate.update("sql/TAttendanceDao/update.sql", entity);
    }
}
