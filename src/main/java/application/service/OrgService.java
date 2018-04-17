package application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.dao.MOrgDao;
import application.entity.MOrg;

/**
 * 組織サービス。
 */
@Service
@Transactional
public class OrgService {

    /** 組織マスタDAO。 */
    @Autowired
    private MOrgDao mOrgDao;

    /**
     * 組織を検索する。
     * @param name 名前
     * @return 組織情報リスト
     */
    public List<MOrg> findOrgs(String name) {
        return mOrgDao.findOrgs(name);
    }

    /**
     * 組織を取得する。
     * @param orgCd 組織コード
     */
    public Optional<MOrg> findOrg(String orgCd) {
        return Optional.ofNullable(mOrgDao.findByOrgCd(orgCd));
    }

    /**
     * 組織を登録する。
     * @param org 組織データ
     */
    public void registerOrg(MOrg org) throws DuplicateKeyException {
        mOrgDao.insert(org);
    }

    /**
     * 組織を更新する。
     * @param org 組織データ
     */
    public void updateOrg(MOrg org) {
        mOrgDao.update(org);
    }

    /**
     * 組織を削除する。
     * @param orgCd 組織コード
     */
    public void deleteOrg(String orgCd) {
        MOrg morg = mOrgDao.findByOrgCd(orgCd);
        mOrgDao.delete(morg);
    }
}
