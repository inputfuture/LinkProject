package com.letv.leauto.ecolink.http.model;

import com.letv.leauto.ecolink.http.host.LetvAutoHosts;

import org.json.JSONException;
import org.json.JSONObject;


public class LeReqData {
    private String operation;
    private String modulename;
    private String tag;
    public String tokenid;
    private int start;
    private int limit;
    private JSONObject queryper;
    public static LeReqData create(String operation, String modulename, String tag) {
        LeReqData data = new LeReqData();
        data.operation = operation;
        data.modulename = modulename;
        data.tag = tag;
        data.tokenid = LetvAutoHosts.TOKEN_ID;
        data.start = 0;
        data.limit = -1;
        data.queryper = new JSONObject();
        return data;
    }
    public void addParam(String key, Object value) {
        try {
            queryper.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setRange(int start, int limit) {
        this.start = start;
        this.limit = limit;
    }
    public String toJSONString()
    {
        try {
            JSONObject data = new JSONObject();
            data.put("start", this.start);
            data.put("limit", this.limit);
            data.put("queryper", this.queryper);
            JSONObject parameter = new JSONObject();
            parameter.put("operation", this.operation);
            parameter.put("modulename", this.modulename);
            parameter.put("tag", this.tag);
            parameter.put("tokenid", tokenid);
            parameter.put("data", data);
            return  "parameter=" + parameter.toString() + "&tokenid=" + LetvAutoHosts.TOKEN_ID;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 点击收藏根据时间返回需要下载的条目列表
     *
     * @param albumId
     * @param start
     * @param end
     * @return
     */
    public  static  JSONObject CreateRequestDataToDownload(String albumId, long start, long end) {
        try {

            JSONObject update_time = new JSONObject();
            update_time.put("start", start);
            update_time.put("end", end);
            JSONObject queryper = new JSONObject();
            queryper.put("ALBUM_ID", albumId);
            queryper.put("CREATE_TIME", update_time);
            JSONObject data = new JSONObject();
            data.put("queryper", queryper);
            JSONObject parameter = new JSONObject();
            parameter.put("modulename", "new_audio_query");
            parameter.put("tag", "ext-gen80");
            parameter.put("operation", "query");
            parameter.put("data", data);
            parameter.put("tokenid", LetvAutoHosts.TOKEN_ID);
            return parameter;
        } catch (JSONException je) {
            return new JSONObject();
        }
    }

    /**
     * 收藏更新接口指令
     *
     * @param albumIds
     * @param tokenid
     * @return
     */
    public  JSONObject CreateRequestDataFavorRefresh(String albumIds, String tag, String tokenid) {
        try {
            JSONObject queryper = new JSONObject();
            queryper.put("AFTER_SORT_ID", albumIds);
            JSONObject data = new JSONObject();
            data.put("queryper", queryper);
            JSONObject parameter = new JSONObject();
            parameter.put("modulename", "collection_query");
            parameter.put("tag", tag);
            parameter.put("operation", "query");
            parameter.put("data", data);
            parameter.put("tokenid", tokenid);
            return parameter;
        } catch (JSONException je) {
            return new JSONObject();
        }
    }

}
