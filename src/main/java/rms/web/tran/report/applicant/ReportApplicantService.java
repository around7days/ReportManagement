package rms.web.tran.report.applicant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import rms.com.consts.MCodeConst;
import rms.com.doma.dao.TReportDao;
import rms.com.doma.entity.TReport;
import rms.web.com.auth.UserInfo;
import rms.web.com.entity.SelectOptionEntity;
import rms.web.mst.user.regist.UserRegistForm;

/**
 * 月報申請画面サービス
 * @author
 */
@Service
public class ReportApplicantService extends rms.com.abstracts.AbstractService {

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(ReportApplicantService.class);

    /** 月報管理テーブルDao */
    @Autowired
    TReportDao tReportDao;

    /** 月報申請画面Dao */
    @Autowired
    ReportApplicantDao reportApplicantDao;

    /**
     * 新規初期処理
     * @param form
     */
    public void initInsert(ReportApplicantForm form) {
        // 表示モードの設定
        form.setViewMode(UserRegistForm.VIEW_MODE_INSERT);

        // 年月のセット
        LocalDateTime date = LocalDateTime.now();
        form.setTargetYear(String.valueOf(date.getYear()));
        form.setTargetMonth(date.format(DateTimeFormatter.ofPattern("MM")));

        // セレクトボックスの設定
        setSelectBox(form);
    }

    /**
     * ファイル保存処理
     * @param file
     * @throws IOException
     * @throws IllegalStateException
     */
    public void saveFile(MultipartFile file) throws IllegalStateException, IOException {
        // ファイルの保存先パスを生成
        Path outputPath = Paths.get("./upload_file", file.getOriginalFilename());
        logger.debug("ファイル保存先 -> {}", outputPath.normalize().toAbsolutePath().toString());

        // ファイル保存
        //        file.transferTo(outputPath.toFile());
        Files.copy(file.getInputStream(), outputPath, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 新規登録処理
     * @param form
     * @param userInfo
     */
    public void insert(ReportApplicantForm form,
                       UserInfo userInfo) {
        // 登録用Entityの生成
        TReport entity = new TReport();
        entity.setApplicantId(userInfo.getUserId());
        entity.setTargetYm(Integer.valueOf(form.getTargetYear() + form.getTargetMonth()));
        entity.setApplicantDate(LocalDateTime.now());
        entity.setApprover1Id(form.getApprover1Id());
        entity.setApprover2Id(form.getApprover2Id());
        entity.setApprover3Id(form.getApprover3Id());
        entity.setFilePath(form.getFile().getOriginalFilename());

        // 承認者の有無に合わせてステータスを設定
        if (!StringUtils.isEmpty(form.getApprover1Id())) {
            entity.setStatus(MCodeConst.A001_Y01);
        } else if (!StringUtils.isEmpty(form.getApprover2Id())) {
            entity.setStatus(MCodeConst.A001_Y02);
        } else {
            entity.setStatus(MCodeConst.A001_Y03);
        }

        // TODO
        tReportDao.delete(entity);

        // 登録処理
        tReportDao.insert(entity);
    }

    /**
     * セレクトボックスの設定
     * @param form
     */
    private void setSelectBox(ReportApplicantForm form) {
        // セレクトボックス用 承認者一覧の取得
        List<SelectOptionEntity> approverList = reportApplicantDao.selectboxApprover();
        approverList.forEach(entity -> logger.debug(entity.toString()));
        // 格納
        form.setApproverList(approverList);
    }

    //    /**
    //     * 更新処理
    //     * @param form
    //     */
    //    public void update(ReportApplicantForm form) {
    //
    //        // 更新情報の生成
    //        MUser mUser = new MUser();
    //        BeanUtils.copyProperties(form, mUser);
    //        logger.debug("更新情報 -> {}", mUser.toString());
    //
    //        // 更新処理
    //        mUserDao.update(mUser);
    //    }
}
