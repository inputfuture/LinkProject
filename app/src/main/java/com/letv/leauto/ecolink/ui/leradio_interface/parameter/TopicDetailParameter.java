package com.letv.leauto.ecolink.ui.leradio_interface.parameter;


import com.letv.leauto.ecolink.ui.leradio_interface.baseparameter.HttpBaseParameter;

public class TopicDetailParameter extends HttpBaseParameter {

    private static final long serialVersionUID = 1L;

    private static final String KEY_ZID = "zid";

    private final String mZid;

    public TopicDetailParameter(String zid) {
        this.mZid = zid;
    }

    @Override
    public HttpBaseParameter combineParams() {
        super.combineParams();
        this.put(KEY_ZID, this.mZid);
        return this;
    }
}
