package com.letv.leauto.ecolink.thincar.module;

/**
 * Created by Administrator on 2017/8/18.
 */

public class ThincarQuickSearchEvent {
    private String mSearchTarget;
    public ThincarQuickSearchEvent(String target) {
        mSearchTarget = target;
    }

    public String getSearchTarget() {
        return  mSearchTarget;
    }
}
