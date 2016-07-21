package rms.web.mst.user.search;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

import rms.com.doma.entity.MUser;

/**
 * ユーザ一覧画面Dao
 */
@Dao
@ConfigAutowireable
public interface UserSearchDao {

    /**
     * ユーザ検索処理
     * @param form
     * @param options
     * @return
     */
    @Select
    List<MUser> searchUser(UserSearchConditionForm form,
                           SelectOptions options);

}