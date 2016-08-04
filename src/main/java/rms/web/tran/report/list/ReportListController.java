package rms.web.tran.report.list;

import java.io.IOException;
import java.nio.file.Path;

import rms.domain.tran.report.entity.ReportSearchConditionEntity;
import rms.domain.tran.report.entity.ReportSearchResultEntity;
import rms.domain.tran.report.service.ReportSelectService;
import rms.web.base.SearchResultEntity;
import rms.web.com.utils.FileUtils;
import rms.web.com.utils.PageInfo;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * 月報一覧画面コントローラー
 * @author
 */
@Controller
@Transactional(rollbackFor = Exception.class)
@SessionAttributes(types = ReportListForm.class)
public class ReportListController extends rms.web.com.abstracts.AbstractController {

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(ReportListController.class);

    /** ページURL */
    private static final String PAGE_URL = "html/reportList";

    /** マッピングURL */
    public static final String MAPPING_URL = "/tran/report/list";

    /** 月報格納ベースディレクトリ */
    @Value("${myapp.report.storage}")
    private String reportStorage;

    /** 月報情報取得サービス */
    @Autowired
    ReportSelectService reportSelectService;

    /**
     * 月報一覧画面フォームの初期化
     * @return
     */
    @ModelAttribute
    ReportListForm setupForm() {
        return new ReportListForm();
    }

    /**
     * 初期処理
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value = MAPPING_URL, params = "init")
    public String initInsert(ReportListForm form,
                             Model model) {
        return PAGE_URL;
    }

    /**
     * 検索処理
     * @param form
     * @param bindingResult
     * @param model
     * @return
     */
    @RequestMapping(value = MAPPING_URL, params = "search")
    public String search(@Validated ReportListForm form,
                         BindingResult bindingResult,
                         Model model) {
        logger.debug("入力フォーム情報 -> {}", form);

        // 入力チェック
        if (bindingResult.hasErrors()) {
            logger.debug("入力チェックエラー -> {}", bindingResult.getAllErrors());
            return PAGE_URL;
        }

        // 検索結果・ページ情報の初期化
        form.setPageInfo(new PageInfo());
        form.setResultList(null);

        // 検索条件の生成
        ReportSearchConditionEntity condition = new ReportSearchConditionEntity();
        BeanUtils.copyProperties(form.getCondition(), condition);

        // 検索処理
        SearchResultEntity<ReportSearchResultEntity> searchResultEntity = reportSelectService.getReportList(condition,
                                                                                                            form.getPageInfo());
        if (searchResultEntity.getResultList().isEmpty()) {
            bindingResult.reject("", "検索結果は存在しません");
            return PAGE_URL;
        }

        // 検索結果をフォームに反映
        form.setResultList(searchResultEntity.getResultList());
        form.getPageInfo().setTotalSize(searchResultEntity.getCount());

        return PAGE_URL;
    }

    /**
     * 再検索処理
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value = MAPPING_URL, params = "reSearch")
    public String reSearch(ReportListForm form,
                           Model model) {
        logger.debug("フォーム情報 -> {}", form);

        // 検索条件の生成
        ReportSearchConditionEntity condition = new ReportSearchConditionEntity();
        BeanUtils.copyProperties(form.getCondition(), condition);

        // 検索処理
        SearchResultEntity<ReportSearchResultEntity> searchResultEntity = reportSelectService.getReportList(condition,
                                                                                                            form.getPageInfo());

        // 検索結果をフォームに反映
        form.setResultList(searchResultEntity.getResultList());
        form.getPageInfo().setTotalSize(searchResultEntity.getCount());

        return PAGE_URL;
    }

    /**
     * 前ページング処理
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value = MAPPING_URL, params = "pagePrev")
    public String pagePrev(ReportListForm form,
                           Model model) {
        // ページング設定
        form.getPageInfo().prev();

        return redirect(MAPPING_URL, "reSearch");
    }

    /**
     * 次ページング処理
     * @param form
     * @param model
     * @return
     */
    @RequestMapping(value = MAPPING_URL, params = "pageNext")
    public String pageNext(ReportListForm form,
                           Model model) {
        // ページング設定
        form.getPageInfo().next();

        return redirect(MAPPING_URL, "reSearch");
    }

    /**
     * 月報DL処理
     * @param form
     * @param index
     * @param response
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(value = MAPPING_URL + "/{index}", params = "download")
    public String download(ReportListForm form,
                           @PathVariable int index,
                           HttpServletResponse response,
                           Model model) throws IOException {
        logger.debug("選択値 -> {}", index);

        // 選択した月報情報
        ReportSearchResultEntity result = form.getResultList().get(index);
        logger.debug("選択月報情報 -> {}", result);

        /*
         * ファイルダウンロード処理
         */
        // ダウンロードファイルパスの生成
        Path filePath = FileUtils.createReportFilePath(reportStorage, result.getApplyUserId(), result.getTargetYm());
        // 月報ダウンロード
        FileUtils.reportDownload(response, filePath);

        return null;
    }
}