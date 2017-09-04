package com.letv.leauto.ecolink.ui.leradio_interface.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 2016/12/22.
 */
public class ChannelListModel extends LeRadioBaseModel {

    public List<Channel> channels;

    public ChannelListModel() {
        channels = new ArrayList<>();
    }

    public List<Channel> getChannels() {
        return this.channels;
    }

    public static ChannelListModel parse(JSONObject object) {

        if (object == null) {
            return null;
        }

        ChannelListModel list = new ChannelListModel();

        try {
            if (object.has(MEDIA_CHANNEL_CHANNELS)) {

                JSONArray chns = object.getJSONArray(MEDIA_CHANNEL_CHANNELS);
                if (chns == null) {
                    return null;
                }
                for (int i = 0; i < chns.length(); i++) {
                    Channel channel = Channel.parse(chns.getJSONObject(i));

                    if (channel != null) {
                        list.channels.add(channel);
                    }
                }
            }

        } catch (Exception e) {

        }
        return list;
    }
}
