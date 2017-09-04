package com.letv.leauto.ecolink.ui.leradio_interface.response;


import com.letv.leauto.ecolink.ui.leradio_interface.data.Channel;
import com.letv.mobile.http.model.LetvHttpBaseModel;

import java.util.List;

/**
 * 标题栏目
 * Data used in home page
 */
public class ChannelResponse extends LetvHttpBaseModel {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    private List<Channel> channels;

    @Override
    public String toString() {
        return "ChannelResponse{" +
                "name='" + name + '\'' +
                ", channels=" + channels +
                '}';
    }
}
