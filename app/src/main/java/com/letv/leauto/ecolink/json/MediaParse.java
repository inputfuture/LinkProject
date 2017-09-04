package com.letv.leauto.ecolink.json;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.database.field.SortType;
import com.letv.leauto.ecolink.database.model.MediaDetail;
import com.letv.leauto.ecolink.http.model.LeObject;
import com.letv.leauto.ecolink.http.utils.DataUtil;
import com.letv.leauto.ecolink.ui.leradio_interface.data.AudioAlbums;
import com.letv.leauto.ecolink.ui.leradio_interface.data.AudioSerchDetail;
import com.letv.leauto.ecolink.ui.leradio_interface.data.VideoAlbums;
import com.letv.leauto.ecolink.utils.Trace;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liweiwei on 16/3/1.
 */
public class MediaParse {


    public static LeObject<MediaDetail> parseMediaResp(String response, boolean isGuess) {
        LeObject<MediaDetail> leObject = new LeObject<MediaDetail>();
        try {
            JSONObject jsonObject = new JSONObject(response);

            int status = jsonObject.optInt("status", 0);
            leObject.tag = jsonObject.optString("tag");
            if (status == 1) {
                JSONObject data = jsonObject.optJSONObject("data");
                if (data != null) {
                    leObject.success = true;
                    leObject.list = getMediaList(data.optJSONArray("root"), isGuess);
                    Trace.Debug("#####parseMediaResp:leObject.list=" + data.optJSONArray("root").toString());
                    if (leObject.list != null && leObject.list.size() > 0) {
                        EcoApplication.LeGlob.getCache().putString(DataUtil.MEDIA_LIST, data.optJSONArray("root").toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return leObject;
    }


    public static ArrayList<MediaDetail> getMediaList(JSONArray jsonArray, boolean isGuess) {
        ArrayList<MediaDetail> subItems = new ArrayList<MediaDetail>();
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    MediaDetail item = new MediaDetail();
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    item.IMG_URL = jsonObject.optString("IMG_URL");
                    item.SOURCE_CP_ID = jsonObject.optString("SOURCE_CP_ID");
                    item.SOURCE_URL = jsonObject.optString("SOURCE_URL");
                    item.NAME = jsonObject.optString("NAME");
                    if (item.NAME != null) {
                        item.NAME = item.NAME.replace("", "").replace("", "");
                    }

                    if (isGuess) {
                        item.AUDIO_ID = jsonObject.optString("MEDIA_ID");
                        item.ALBUM_ID = jsonObject.optString("CLASS_ID");
                    } else {
                        item.AUDIO_ID = jsonObject.optString("AUDIO_ID");
                        item.ALBUM_ID = jsonObject.optString("ALBUM_ID");
                    }
                    if (jsonObject.has("START_TIME")&&jsonObject.has("END_TIME")){
                        item.START_TIME = jsonObject.optLong("START_TIME");
                        item.END_TIME = jsonObject.optLong("END_TIME");
                    }
                    item.AUTHOR = jsonObject.optString("AUTHOR");
                    item.CREATE_TIME = jsonObject.optString("CREATE_TIME");
                    item.LE_SOURCE_MID = jsonObject.optString("LE_SOURCE_MID");
                    item.LE_SOURCE_VID = jsonObject.optString("LE_SOURCE_VID");
                    item.TYPE = jsonObject.optString("TYPE");
                    item.playType = jsonObject.optString("PlayType");
                    item.setSourceName(jsonObject.optString("SourceName"));
                    if(item.getSourceName()!=null &&(item.getSourceName().contains("虾米")||item.getSourceName().contains("Xiami"))){
                        item.XIA_MI_ID = item.SOURCE_CP_ID;
                    }else {
                        item.XIA_MI_ID = jsonObject.optString("XIA_MI_ID");
                    }
                    subItems.add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return subItems;
    }

    public static ArrayList<VideoAlbums> getVideoAlbumsList(JSONArray jsonArray) {
        ArrayList<VideoAlbums> subItems = new ArrayList<VideoAlbums>();
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    VideoAlbums item = new VideoAlbums();
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    item.setName(jsonObject.optString("name"));
                    item.setAlbumId(jsonObject.optString("albumId"));
                    item.setRating(jsonObject.optString("rating"));
                    JSONArray starring = jsonObject.getJSONArray("starring");
                    ArrayList<String> starringList = new ArrayList<String>();
                    if (starring != null && starring.length() > 0) {
                        for (int j = 0; j < starring.length(); j++) {
                            String starringItem = starring.getString(i);
                            starringList.add(starringItem);
                        }
                    }
                    item.setStarring(starringList);
                    JSONArray directory = jsonObject.getJSONArray("directory");
                    ArrayList<String> directoryList = new ArrayList<String>();
                    if (directory != null && directory.length() > 0) {
                        for (int j = 0; j < directory.length(); j++) {
                            String directoryItem = directory.getString(i);
                            directoryList.add(directoryItem);
                        }
                    }
                    item.setDirectory(directoryList);
                    item.setImg(jsonObject.optString("images"));

                    item.setType(jsonObject.optString("type"));
                    subItems.add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return subItems;
    }

    public static ArrayList<AudioAlbums> getAudioAlbumsList(JSONArray jsonArray) {
        ArrayList<AudioAlbums> subItems = new ArrayList<AudioAlbums>();
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    AudioAlbums item = new AudioAlbums();
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    item.setName(jsonObject.optString("name"));
                    item.setAlbumId(jsonObject.optString("albumId"));
                    item.setAuthor(jsonObject.optString("singer"));
                    item.setImg(jsonObject.optString("images"));
                    JSONObject sourceObject = jsonObject.optJSONObject("source");
                    if(sourceObject!=null) {
                        if(sourceObject.has("sourceName")) {
                            item.setSourceName(sourceObject.optString("sourceName"));
                        }
                        if(sourceObject.has("sourceId")) {
                            item.setSourceId(sourceObject.optString("sourceId"));
                        }
                    }
                    subItems.add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return subItems;
    }

    public static ArrayList<MediaDetail> getAudioSerchDetailList(JSONArray jsonArray) {
        ArrayList<AudioSerchDetail> subItems = new ArrayList<AudioSerchDetail>();
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    AudioSerchDetail item = new AudioSerchDetail();
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    item.setName(jsonObject.optString("name"));
                    item.setSubName(jsonObject.optString("subName"));
                    item.setCategoryId(jsonObject.optString("categoryId"));
                    item.setAlbumId(jsonObject.optString("albumId"));
                    item.setMediaId(jsonObject.optString("mediaId"));
                    item.setMediaType(jsonObject.optString("mediaType"));
                    item.setAuthor(jsonObject.optString("author"));
                    item.setEpisode(jsonObject.optString("episode"));
                    item.setImg(jsonObject.optString("images"));
                    item.setDuration(jsonObject.optString("duration"));
                    item.setPage(jsonObject.optString("page"));
                    item.setPlayUrl(jsonObject.optString("playUrl"));
                    JSONObject sourceObject = jsonObject.optJSONObject("source");
                    if(sourceObject!=null) {
                        if(sourceObject.has("sourceName")) {
                            item.setSourceName(sourceObject.optString("sourceName"));
                        }
                        if(sourceObject.has("sourceType")) {
                            item.setSourceType(sourceObject.optString("sourceType"));
                        }
                        if(sourceObject.has("sourceId")) {
                            item.setSourceId(sourceObject.optString("sourceId"));
                        }
                    }
                    subItems.add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ArrayList<MediaDetail> MediaList = changeSearch2MediaList(subItems);
        return MediaList;
    }

    // 将搜索接口拿到的曲目列表转化为播放的列表
    private static ArrayList<MediaDetail> changeSearch2MediaList(ArrayList<AudioSerchDetail> models) {
        ArrayList<MediaDetail> MediaList = new ArrayList<MediaDetail>();
        if (models != null) {
            for (int i = 0; i < models.size(); i++) {
                AudioSerchDetail item = models.get(i);
                MediaDetail info = new MediaDetail();
                info.NAME = (item.getName());//歌曲名称
                info.AUDIO_ID = item.getMediaId();//歌曲id
                info.IMG_URL = (item.getImg());//图片的Url
                info.ALBUM_ID = item.getAlbumId();////专辑id
                info.TYPE = SortType.SORT_LE_RADIO;//自己区分歌曲的类型
                if(item.getDuration()!=null && item.getDuration()!="") {
                    info.setDuration(Integer.parseInt(item.getDuration()));//歌曲的时长，单位s
                }
                info.ALBUM = item.getName();//专辑名称
                info.AUTHOR = item.getAuthor();
                info.SOURCE_URL = item.getPlayUrl();
                info.playType = item.getMediaType();
                String sourceName = item.getSourceName();
                if((sourceName==null||(sourceName!=null && sourceName.equals("null"))) && item.getSourceType()!=null){
                    sourceName = item.getSourceType();
                }
                info.setSourceName(sourceName);
                info.SOURCE_CP_ID = item.getSourceId();
                MediaList.add(info);
            }
        } else {
            MediaList = null;
        }
        return MediaList;
    }
}
