package com.letv.leauto.ecolink.ui.leradio_interface.parameter;

import com.letv.leauto.ecolink.http.host.LetvAutoHosts;
import com.letv.leauto.ecolink.ui.leradio_interface.baseparameter.HttpBaseParameter;

import org.json.JSONObject;

/**
 * Created by kevin on 2016/12/22.
 */
public class SearchParameter extends HttpBaseParameter {

    public static final String KEY_PARAMS = "params";

    public static final String KEY_WD = "wd";

    public static final String KEY_PAGE = "page";

    public static final String KEY_ROWS = "rows";

    public static final String KEY_UID = "uid";

    public static final String KEY_DT = "dt";

    public static final String KEY_PRIORITY = "albumPriority";

    /* 查询当前页，从1开始，如果不分页的话，不填，默认为1 */
    private String page;

    /*查询数据条数，如果不分页的话，不填，默认查询前20条数据*/
    private String rows;

    /* 乐视用户id，如果用户登录必填，如果未登录可不填 */
    private String uid;

    /* 查询内容（必填）*/
    private String wd;

    /* 1-视频专辑；6-直播；8-音乐专辑；9-单条音乐） */
    private String dt;

    /* 1.专辑优先（如果同时查询出专辑列表和单条音乐列表，则返回专辑列表的第一个专辑返回，如果只查询出单条音乐列表，则返回此音乐列表）
            0.无优先，查询出什么数据返回什么数据 */
    private String albumPriority;


    public SearchParameter(String wd) {
        this.wd = wd;
    }

    public SearchParameter builder(String key, String value) {
        this.put(key, value);
        if (KEY_PAGE.equals(key)) {
            this.page = value;
        } else if (KEY_DT.equals(key)) {
            this.dt = value;
        } else if (KEY_ROWS.equals(key)) {
            this.rows = value;
        } else if (KEY_WD.equals(key)) {
            this.wd = value;
        } else if (KEY_PRIORITY.equals(key)) {
            this.albumPriority = value;
        }
        return this;
    }

    @Override
    public HttpBaseParameter combineParams() {
        super.combineParams();
        this.put(KEY_WD, wd);
        return this;
    }

    @Override
    public String toJSONString() {
        JSONObject parameter = new JSONObject();
        try {

            parameter.put(KEY_DT, this.dt);
            parameter.put(KEY_WD, this.wd);
            parameter.put(KEY_PAGE, this.page);
            parameter.put(KEY_ROWS, this.rows);
            parameter.put(KEY_PRIORITY, this.albumPriority);
            parameter.put(KEY_UID, this.uid);
        } catch (Exception e) {

        }

        return  KEY_PARAMS + "=" + parameter.toString();
    }
}
