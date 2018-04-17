package application.dao;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import application.context.AdminUser;
import application.emuns.DelFlag;
import application.entity.AbstractEntity;

/**
 * DAO共通抽象クラス
 */
abstract class AbstractDao<T extends AbstractEntity> {
    abstract int insert(T entity);

    abstract int update(T entity);

    /**
     * 論理削除する。
     * @param entity 削除対象
     * @return 処理件数
     */
    public int delete(T entity) {
        entity.setDelFlg(DelFlag.ON.getVal());
        setUpdateColumns(entity);
        return update(entity);
    }

    /**
     * 挿入または更新する。
     * @param entity 保存内容
     * @return 処理件数
     */
    public int save(T entity) {
        int res;
        if (entity.getUpdateDate() == null) {
            setInsertColumns(entity);
            res = insert(entity);
        } else {
            setUpdateColumns(entity);
            res = update(entity);
        }
        return res;
    }

    /**
     * INSERT用の共通カラム値を設定する。
     * @param entity 設定先のエンティティ
     */
    public void setInsertColumns(T entity) {
        entity.setRegistDate(LocalDateTime.now());
        entity.setRegistUserId(0);
        entity.setRegistFuncCd("0");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            AdminUser principal = (AdminUser) authentication.getPrincipal();
            entity.setRegistUserId(principal.getUser().getUserId());
        }
    }

    /**
     * UPDATE用の共通カラム値を設定する。
     * @param entity 設定先のエンティティ
     */
    public void setUpdateColumns(T entity) {
        entity.setUpdateDate(LocalDateTime.now());
        entity.setUpdateUserId(0);
        entity.setUpdateFuncCd("0");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            AdminUser principal = (AdminUser) authentication.getPrincipal();
            entity.setUpdateUserId(principal.getUser().getUserId());
        }
    }
}
