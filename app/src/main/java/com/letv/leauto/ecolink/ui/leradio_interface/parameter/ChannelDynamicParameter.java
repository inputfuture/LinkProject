package com.letv.leauto.ecolink.ui.leradio_interface.parameter;

import com.letv.leauto.ecolink.ui.leradio_interface.baseparameter.HttpBaseParameter;

/**
 * 栏目列表参数
 * Dynamic parameter for home page
 * @author luoyanfeng@le.com
 */
public class ChannelDynamicParameter extends HttpBaseParameter {

    //
    @Override
    public HttpBaseParameter combineParams() {
        super.combineParams();
        return this;
    }

}
