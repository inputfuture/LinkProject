package com.letv.leauto.ecolink.json;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.database.model.LeSortInfo;
import com.letv.leauto.ecolink.http.model.LeObject;
import com.letv.leauto.ecolink.http.utils.DataUtil;
import com.letv.leauto.ecolink.ui.leradio_interface.data.Channel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liweiwei on 16/2/24.
 */
public class ChannelParse {

    public static LeObject<LeSortInfo> parseSortResp(String response) {
        LeObject<LeSortInfo> leObject = new LeObject<LeSortInfo>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            leObject.tag = jsonObject.optString("tag");
            int status = jsonObject.optInt("status", 0);
            if (status == 1) {
                JSONObject data = jsonObject.optJSONObject("data");
                if (data != null) {
                    leObject.success = true;
                    leObject.list = getSortInfoList(data.optJSONArray("root"));
                    if (leObject.list != null && leObject.list.size() > 0) {
                        EcoApplication.LeGlob.getCache().putString(DataUtil.CHANNEL_LIST, data.optJSONArray("root").toString());
                    }
                }
            }
        } catch (Exception e) {

        }
        return leObject;
    }

    public static ArrayList<LeSortInfo> getSortInfoList(JSONArray jsonArray) {
        ArrayList<LeSortInfo> sortInfos = new ArrayList<LeSortInfo>();
        sortInfos.add(new LeSortInfo(EcoApplication.instance.getString(R.string.music_lable_hot), null, "8999"));
        sortInfos.add(new LeSortInfo(EcoApplication.instance.getString(R.string.music_lable_my), null, ""));
        sortInfos.add(new LeSortInfo(EcoApplication.instance.getString(R.string.music_lable_talkshow), null, "600"));
        sortInfos.add(new LeSortInfo(EcoApplication.instance.getString(R.string.music_lable_live), null, "2400"));
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                LeSortInfo item = new LeSortInfo();
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                item.SORT_ID = jsonObject.optString("PAGE_ID");
                item.NAME = jsonObject.optString("NAME");
                item.TYPE = jsonObject.optString("TYPE");
                if (!item.NAME.equals(EcoApplication.instance.getString(R.string.music_lable_talkshow)) &&
                        !item.NAME.equals(EcoApplication.instance.getString(R.string.music_lable_my)) &&
                        !item.NAME.equals(EcoApplication.instance.getString(R.string.music_lable_live)) &&
                        !item.NAME.equals(EcoApplication.instance.getString(R.string.music_lable_hot)) &&
                        !item.NAME.equals(EcoApplication.instance.getString(R.string.main_nav_local)) &&
                        !item.NAME.equals(EcoApplication.instance.getString(R.string.main_nav_magical)) &&
                        !item.NAME.equals(EcoApplication.instance.getString(R.string.main_nav_collection)) &&
                        !item.NAME.equals(EcoApplication.instance.getString(R.string.main_nav_recent))) {
                    sortInfos.add(item);
                }
            }
        }
        return sortInfos;
    }

    public static ArrayList<Channel> getChannelList(JSONArray jsonArray) {
        ArrayList<Channel> channels=new ArrayList<>();
//        channels.add(new Channel("1003436801","热门","","1","http://d.itv.letv.com/mobile/channel/data.json?pageid=1003436801","14021151","2"));
//        channels.add(new Channel("1003573514","我的","","1","http://d.itv.letv.com/mobile/channel/data.json?pageid=1003573514","14021172","2"));
//        channels.add(new Channel("1003573513","音乐","","1","http://d.itv.letv.com/mobile/channel/data.json?pageid=1003573513","14021170","2"));
//        channels.add(new Channel("1003436873","直播","","1","http://d.itv.letv.com/mobile/channel/data.json?pageid=1003436873","14021152","2"));
        int count=jsonArray.length();
        for (int i = 0; i < count; i++) {
            try {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                Channel channel=new Channel();
                channel.setPageId(jsonObject.optString("pageId"));
                channel.setName(jsonObject.optString("name"));
                channel.setSkipType(jsonObject.optString("skipType"));
                channel.setCmsID(jsonObject.optString("cmsID"));
                channel.setDataUrl(jsonObject.optString("dataUrl"));

                channel.setType(jsonObject.optString("type"));
                channel.setMzcId(jsonObject.optString("mzcId"));
                    channels.add(channel);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return channels;
    }
}
