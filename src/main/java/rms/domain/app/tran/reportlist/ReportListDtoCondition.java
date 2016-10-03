package rms.domain.app.tran.reportlist;

/**
 * 月報一覧（検索条件）クラス
 * @author
 */
public class ReportListDtoCondition extends rms.common.abstracts.AbstractDto {

    /* 変数宣言 ------------------------------------------------------------- */
    /** 対象年月 */
    private Integer targetYm;
    /** 申請者ID */
    private String applyUserId;
    /** 申請者名 */
    private String applyUserNm;

    /* getter/setter -------------------------------------------------------- */
    /**
     * 対象年月を取得します。
     * @return 対象年月
     */
    public Integer getTargetYm() {
        return targetYm;
    }

    /**
     * 対象年月を設定します。
     * @param targetYm 対象年月
     */
    public void setTargetYm(Integer targetYm) {
        this.targetYm = targetYm;
    }

    /**
     * 申請者IDを取得します。
     * @return 申請者ID
     */
    public String getApplyUserId() {
        return applyUserId;
    }

    /**
     * 申請者IDを設定します。
     * @param applyUserId 申請者ID
     */
    public void setApplyUserId(String applyUserId) {
        this.applyUserId = applyUserId;
    }

    /**
     * 申請者名を取得します。
     * @return 申請者名
     */
    public String getApplyUserNm() {
        return applyUserNm;
    }

    /**
     * 申請者名を設定します。
     * @param applyUserNm 申請者名
     */
    public void setApplyUserNm(String applyUserNm) {
        this.applyUserNm = applyUserNm;
    }
}
