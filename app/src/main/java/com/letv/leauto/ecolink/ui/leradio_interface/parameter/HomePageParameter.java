package com.letv.leauto.ecolink.ui.leradio_interface.parameter;

import com.letv.leauto.ecolink.ui.leradio_interface.baseparameter.HttpBaseParameter;


/**
 * Created by Administrator on 2016/11/7.
 */
public class HomePageParameter extends HttpBaseParameter {

    private static final String KEY_PAGEID = "pageid";
    private static final String KEY_PAGE = "page";

    private String mPageid;

    private String mPage;

    public HomePageParameter(String pageid,String pageIndex) {
        mPageid = pageid;
        mPage = pageIndex;
    }

    @Override
    public HttpBaseParameter combineParams() {
        super.combineParams();
        this.put(KEY_PAGEID, mPageid);
        this.put(KEY_PAGE, mPage);
        return this;
    }

}
