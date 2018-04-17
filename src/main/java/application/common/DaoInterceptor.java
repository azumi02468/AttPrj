package application.common;

import java.time.LocalDateTime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import application.emuns.ApplicationErrors;
import application.entity.AbstractEntity;
import application.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;

/**
 * DAOインターセプター
 * 共通項目の設定と更新日時による楽観的ロック制御を行う。
 */

@Slf4j
@Aspect
@Component
@SuppressWarnings("unused")
public class DaoInterceptor {
    @Around("target(application.dao.AbstractDao) && execution(public int insert(application.entity.AbstractEntity))")
    public Object setCommonFieldsForRegister(ProceedingJoinPoint jp) throws Throwable {
        AbstractEntity entity = (AbstractEntity) jp.getArgs()[0];
        // ログインユーザIDの特定
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            final User loginUser = (User) authentication.getPrincipal();
            entity.setRegistUserId(Integer.valueOf(loginUser.getUsername()));
        } else {
            entity.setRegistUserId(0);
        }

        // 操作機能の特定
        // TODO:リクエストURLパスからregistFuncCdをセット
        entity.setRegistFuncCd("0");

        // 登録日時設定
        entity.setRegistDate(LocalDateTime.now());

        // insertを続行
        return jp.proceed(new Object[] { entity });
    }

    @Around("target(application.dao.AbstractDao) && execution(public int update(application.entity.AbstractEntity))")
    public Object setCommonFieldsForUpdate(ProceedingJoinPoint jp) throws Throwable {
        AbstractEntity entity = (AbstractEntity) jp.getArgs()[0];

        // ログインユーザIDの特定
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            final User loginUser = (User) authentication.getPrincipal();
            entity.setUpdateUserId(Integer.valueOf(loginUser.getUsername()));
        } else {
            entity.setUpdateUserId(0);
        }

        // 操作機能の特定
        // TODO:リクエストURLパスからupdateFuncCdをセット
        entity.setUpdateFuncCd("0");

        // 更新日時設定
        entity.setUpdateDate(LocalDateTime.now());

        // updateを続行
        Integer upCount = (Integer) jp.proceed(new Object[] { entity });

        // 結果チェック
        if (upCount == 0) {
            throw new ApplicationException(ApplicationErrors.ALREADY_UPDATED);
        }
        return upCount;
    }

}
