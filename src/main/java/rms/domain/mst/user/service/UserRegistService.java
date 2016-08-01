package rms.domain.mst.user.service;

import rms.com.consts.Const;
import rms.com.consts.MRoleConst;
import rms.domain.com.entity.MUser;
import rms.domain.com.entity.MUserRole;
import rms.domain.com.repository.MUserDao;
import rms.domain.com.repository.MUserRoleDao;
import rms.domain.mst.user.repository.UserRegistDao;
import rms.domain.mst.user.repository.UserSelectDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ユーザ情報登録サービス
 * @author
 */
@Service
public class UserRegistService extends rms.domain.com.abstracts.AbstractService {
    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(UserRegistService.class);

    /** ユーザマスタDao */
    @Autowired
    MUserDao mUserDao;

    /** ユーザ役割マスタDao */
    @Autowired
    MUserRoleDao mUserRoleDao;

    /** ユーザ情報取得Dao */
    @Autowired
    UserSelectDao userSelectDao;

    /** ユーザ情報登録Dao */
    @Autowired
    UserRegistDao userRegistDao;

    /**
     * ユーザマスタ新規登録処理
     * @param mUser
     */
    public void insertUserMst(MUser mUser) {
        logger.debug("登録情報 -> {}", mUser);

        // 登録処理
        mUserDao.insert(mUser);
    }

    /**
     * ユーザ役割マスタ登録処理<br>
     * 説明：ユーザに紐付くユーザ役割マスタを全て削除してから新規登録を行います。
     * @param userId
     * @param applicantFlg
     * @param approverFlg
     * @param adminFlg
     */
    public void deleteInsertUserRoleMst(String userId,
                                        String applicantFlg,
                                        String approverFlg,
                                        String adminFlg) {
        logger.debug("登録情報 -> 申請者:{}  承認者:{}  管理者:{}", applicantFlg, approverFlg, adminFlg);

        // ユーザに紐付く全役割の削除
        userRegistDao.deleteUserRoleByUserId(userId);

        // ユーザ役割マスタ情報の生成
        MUserRole mUserRole = new MUserRole();
        mUserRole.setUserId(userId);

        // 申請者の登録
        if (Const.FLG_ON.equals(applicantFlg)) {
            mUserRole.setRole(MRoleConst.APPLICANT);
            mUserRoleDao.insert(mUserRole);
        }
        // 承認者の登録
        if (Const.FLG_ON.equals(approverFlg)) {
            mUserRole.setRole(MRoleConst.APPROVER);
            mUserRoleDao.insert(mUserRole);
        }
        // 管理者者の登録
        if (Const.FLG_ON.equals(adminFlg)) {
            mUserRole.setRole(MRoleConst.ADMIN);
            mUserRoleDao.insert(mUserRole);
        }
    }

    /**
     * 更新処理
     * @param mUser
     */
    public void updateUserMst(MUser mUser) {
        logger.debug("更新情報 -> {}", mUser);

        // 更新処理（楽観的排他制御）
        mUserDao.update(mUser);
    }
}