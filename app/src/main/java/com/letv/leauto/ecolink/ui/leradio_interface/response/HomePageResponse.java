package com.letv.leauto.ecolink.ui.leradio_interface.response;


import com.letv.leauto.ecolink.ui.leradio_interface.data.ChannelBlock;
import com.letv.mobile.http.model.LetvHttpBaseModel;

import java.util.List;

/**
 * 首页使用的数据
 * Data used in home page
 */
public class HomePageResponse extends LetvHttpBaseModel {
    /**
     * 其他区块数据
     * Block's data
     */
    private List<ChannelBlock> block;

    public List<ChannelBlock> getBlock() {
        return this.block;
    }

    public void setBlock(List<ChannelBlock> block) {
        this.block = block;
    }

}
