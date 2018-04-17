package application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.dao.MDivDao;
import application.emuns.Division;
import application.entity.MDivDetail;

/**
 * 区分サービス
 */
@Service
@Transactional
public class DivisionService {
    @Autowired
    private MDivDao mDivDao;

    /**
     * 権限コード区分明細を取得する。
     *
     * @return 区分明細情報リスト
     */
    public List<MDivDetail> getAuthList() {
        return mDivDao.findDetailById(Division.AUTH.getId());
    }

    /**
     * 権限コード区分明細を取得する。
     * @param divCd 区分コード
     * @return 区分明細情報
     */
    public Optional<MDivDetail> getAuth(String divCd) {
        return Optional.ofNullable(mDivDao.findDetailByIdAndCd(Division.AUTH.getId(), divCd));
    }
}
