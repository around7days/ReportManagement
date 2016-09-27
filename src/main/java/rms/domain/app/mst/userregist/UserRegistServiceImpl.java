package rms.domain.app.mst.userregist;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rms.common.base.BusinessException;
import rms.common.consts.Const;
import rms.common.consts.MRoleConst;
import rms.common.dao.MUserApproveFlowDao;
import rms.common.dao.MUserDao;
import rms.common.dao.MUserRoleDao;
import rms.common.dao.VMUserDao;
import rms.common.model.MUser;
import rms.common.model.MUserApproveFlow;
import rms.common.model.MUserRole;
import rms.common.model.VMUser;
import rms.common.utils.BeanUtils;
import rms.common.utils.SelectOptionEntity;
import rms.common.utils.StringUtils;

/**
 * ユーザ登録画面サービス
 * @author
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserRegistServiceImpl implements UserRegistService {
    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(UserRegistServiceImpl.class);

    /** ユーザ情報登録Dao */
    @Autowired
    UserRegistDao dao;

    /** MUserDao */
    @Autowired
    MUserDao mUserDao;

    /** MUserApproveFlowDao */
    @Autowired
    MUserApproveFlowDao mUserApproveFlowDao;

    /** MUserRoleDao */
    @Autowired
    MUserRoleDao mUserRoleDao;

    /** VMUserDao */
    @Autowired
    VMUserDao vMUserDao;

    @Autowired
    protected MessageSource message;

    /**
     * ユーザ情報の取得
     * @param userId
     * @return
     */
    @Override
    public UserRegistEntity getUserInfo(String userId) {
        // ユーザマスタ情報の取得
        VMUser vMUser = vMUserDao.selectById(userId);

        // ユーザ役割マスタ情報の取得
        List<MUserRole> mUserRoleList = dao.userRoleListByUserId(userId);

        // 返却用ユーザ情報の生成
        UserRegistEntity userRegistEntity = new UserRegistEntity();

        // 値の設定
        // ユーザ情報
        org.springframework.beans.BeanUtils.copyProperties(vMUser, userRegistEntity);
        // 役割情報
        for (MUserRole mUserRole : mUserRoleList) {
            switch (mUserRole.getRole()) {
            case MRoleConst.APPLY: //申請者
                userRegistEntity.setRoleApplyFlg(Const.FLG_ON);
                break;
            case MRoleConst.APPROVE: //承認者
                userRegistEntity.setRoleApproveFlg(Const.FLG_ON);
                break;
            case MRoleConst.ADMIN: //管理者者
                userRegistEntity.setRoleAdminFlg(Const.FLG_ON);
                break;
            }
        }

        logger.debug("取得情報 -> {}", userRegistEntity);

        return userRegistEntity;
    }

    /**
     * ユーザ情報登録処理
     * @param userRegistEntity
     * @throws BusinessException
     */
    @Override
    public void regist(UserRegistEntity userRegistEntity) throws BusinessException {

        /*
         * 業務ロジックチェック
         */
        // ユーザIDの重複チェック
        validateUniquUserId(userRegistEntity.getUserId());
        // 承認ルート設定チェック
        validateApprovalRoute(userRegistEntity.getRoleApplyFlg(),
                              userRegistEntity.getApproveUserId1(),
                              userRegistEntity.getApproveUserId2(),
                              userRegistEntity.getApproveUserId3());

        /*
         * ユーザマスタ登録
         */
        inserUser(userRegistEntity);

        /*
         * ユーザ承認フローマスタ登録
         */
        deleteInsertUserApproveFlow(userRegistEntity);

        /*
         * ユーザ役割マスタ登録
         */
        deleteInsertUserRole(userRegistEntity);
    }

    /**
     * ユーザ情報更新処理
     * @param userRegistEntity
     * @throws BusinessException
     */
    @Override
    public void update(UserRegistEntity userRegistEntity) throws BusinessException {

        /*
         * 業務ロジックチェック
         */
        // 承認ルート設定チェック
        validateApprovalRoute(userRegistEntity.getRoleApplyFlg(),
                              userRegistEntity.getApproveUserId1(),
                              userRegistEntity.getApproveUserId2(),
                              userRegistEntity.getApproveUserId3());

        /*
         * ユーザマスタ更新
         */
        updateUser(userRegistEntity);

        /*
         * ユーザ承認フローマスタ登録
         */
        deleteInsertUserApproveFlow(userRegistEntity);

        /*
         * ユーザ役割マスタ登録
         */
        deleteInsertUserRole(userRegistEntity);
    }

    /**
     * ユーザマスタ登録処理
     * @param userRegistEntity
     */
    private void inserUser(UserRegistEntity userRegistEntity) {
        // 登録情報の生成
        MUser mUser = BeanUtils.createCopyProperties(userRegistEntity, MUser.class);
        // 登録処理
        mUserDao.insert(mUser);
    }

    /**
     * ユーザ承認フローマスタ登録処理<br>
     * 説明：ユーザIDに紐付く承認フローを全て削除してから新規登録を行います。
     * @param userRegistEntity
     */
    private void deleteInsertUserApproveFlow(UserRegistEntity userRegistEntity) {

        // ユーザに紐付く全承認フローの削除
        dao.deleteUserApproveFlowByUserId(userRegistEntity.getUserId());

        // ユーザ役割マスタ情報の生成
        MUserApproveFlow mUserApproveFlow = new MUserApproveFlow();
        mUserApproveFlow.setUserId(userRegistEntity.getUserId());

        // 承認者１
        mUserApproveFlow.setApproveSeq(Const.APPROVE_FLOW_SEQ_1);
        mUserApproveFlow.setApproveUserId(userRegistEntity.getApproveUserId1());
        mUserApproveFlowDao.insert(mUserApproveFlow);
        // 承認者２
        mUserApproveFlow.setApproveSeq(Const.APPROVE_FLOW_SEQ_2);
        mUserApproveFlow.setApproveUserId(userRegistEntity.getApproveUserId2());
        mUserApproveFlowDao.insert(mUserApproveFlow);
        // 承認者３
        mUserApproveFlow.setApproveSeq(Const.APPROVE_FLOW_SEQ_3);
        mUserApproveFlow.setApproveUserId(userRegistEntity.getApproveUserId3());
        mUserApproveFlowDao.insert(mUserApproveFlow);
    }

    /**
     * ユーザ役割マスタ登録処理<br>
     * 説明：ユーザに紐付くユーザ役割マスタを全て削除してから新規登録を行います。
     * @param userRegistEntity
     */
    private void deleteInsertUserRole(UserRegistEntity userRegistEntity) {

        // ユーザに紐付く全役割の削除
        dao.deleteUserRoleByUserId(userRegistEntity.getUserId());

        // ユーザ役割マスタ情報の生成
        MUserRole mUserRole = new MUserRole();
        mUserRole.setUserId(userRegistEntity.getUserId());

        // 申請者の登録
        if (Const.FLG_ON.equals(userRegistEntity.getRoleApplyFlg())) {
            mUserRole.setRole(MRoleConst.APPLY);
            mUserRoleDao.insert(mUserRole);
        }
        // 承認者の登録
        if (Const.FLG_ON.equals(userRegistEntity.getRoleApproveFlg())) {
            mUserRole.setRole(MRoleConst.APPROVE);
            mUserRoleDao.insert(mUserRole);
        }
        // 管理者者の登録
        if (Const.FLG_ON.equals(userRegistEntity.getRoleAdminFlg())) {
            mUserRole.setRole(MRoleConst.ADMIN);
            mUserRoleDao.insert(mUserRole);
        }
    }

    /**
     * ユーザマスタ更新処理
     * @param userRegistEntity
     */
    private void updateUser(UserRegistEntity userRegistEntity) {
        // 更新情報の生成
        MUser mUser = BeanUtils.createCopyProperties(userRegistEntity, MUser.class);

        // 更新処理（楽観的排他制御）
        mUserDao.update(mUser);
    }

    /**
     * セレクトボックス用 承認者一覧情報の取得
     * @return
     */
    @Override
    public List<SelectOptionEntity> getSelectboxApprove() {
        // セレクトボックス用 承認者一覧の取得
        List<SelectOptionEntity> approveList = dao.selectboxApproveUser();

        return approveList;
    }

    /**
     * セレクトボックス用 部署一覧情報の取得
     * @return
     */
    @Override
    public List<SelectOptionEntity> getSelectboxDepartment() {
        // セレクトボックス用 部署一覧の取得
        List<SelectOptionEntity> departmentList = dao.selectboxApproveUser();

        return departmentList;
    }

    /**
     * ユーザIDの重複チェック
     * @param userId
     * @throws BusinessException
     */
    private void validateUniquUserId(String userId) throws BusinessException {
        MUser mUser = mUserDao.selectById(userId);
        if (mUser != null) {
            List<Object> params = Arrays.asList("ユーザIDが");
            throw new BusinessException(message.getMessage("error.001", params.toArray(), Locale.getDefault()));
        }
    }

    /**
     * 承認ルート設定チェック<br>
     * ・承認者１～３に同じ承認者は設定できません。<br>
     * ・役割に申請者を保持している場合、承認者３は必須入力になります。
     * @param roleApplyFlg
     * @param approveUserId1
     * @param approveUserId2
     * @param approveUserId3
     * @throws BusinessException
     */
    private void validateApprovalRoute(String roleApplyFlg,
                                       String approveUserId1,
                                       String approveUserId2,
                                       String approveUserId3) throws BusinessException {
        //@formatter:off
        if (isValueEquals(approveUserId1, approveUserId2) ||
            isValueEquals(approveUserId1, approveUserId3) ||
            isValueEquals(approveUserId2, approveUserId3)) {
            // 承認者１～３に同じ承認者は設定できません
            throw new BusinessException(message.getMessage("error.004", null, Locale.getDefault()));
        }
        //@formatter:on

        if (Const.FLG_ON.equals(roleApplyFlg) && StringUtils.isEmpty(approveUserId3)) {
            // 役割が申請者の場合、承認者３は必須です
            throw new BusinessException(message.getMessage("error.005", null, Locale.getDefault()));
        }
    }

    /**
     * 値の同一チェック<br>
     * （どちらかの値が空白の場合はfalseとする）
     * @param value1
     * @param value2
     * @return true:同じ false:異なる
     */
    private boolean isValueEquals(String value1,
                                  String value2) {
        if (StringUtils.isEmpty(value1) || StringUtils.isEmpty(value2)) {
            return false;
        }
        return value1.equals(value2);
    }

}
