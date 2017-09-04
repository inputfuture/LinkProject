package com.letv.leauto.ecolink.ui.leradio_interface.data;

import java.util.List;

/**
 * Created by kevin on 2016/12/22.
 */
public class LeRadioBaseModel {

    /********** 专辑列表 **************/
    /* JSON 数据的根 */
    public static final String MEDIA_LIST_DATA = "data";

    /* album列表 */
    public static final String MEDIA_LIST_ALMBU = "audios";

    /* 剧集列表：专辑分类id */
    public static final String MEDIA_LIST_CATEGORY_ID = "categoryId";

    /* 剧集列表：专辑id（音/视频） */
    public static final String MEDIA_LIST_ALBUM_ID = "albumId";

    /* 剧集列表：专辑下详情id */
    public static final String MEDIA_LIST_MEDIAID = "mediaId";

    /* 剧集列表：标题 */
    public static final String MEDIA_LIST_NAME = "name";

    /* 剧集列表：副标题 */
    public static final String MEDIA_LIST_SUBNAME = "subName";

    /* 剧集列表：音频时长 */
    public static final String MEDIA_LIST_DURATION = "duration";

    /* 剧集列表：音频来源 */
    public static final String MEDIA_LIST_SOURCENAME = "sourceName";

    /* 剧集列表：作者 */
    public static final String MEDIA_LIST_AUTHOR = "author";

    /* 剧集列表：集数 */
    public static final String MEDIA_LIST_EPISODE = "episode";

    /* 剧集列表：页码 */
    public static final String MEDIA_LIST_PAGE = "page";

    /* 剧集列表：图片 */
    public static final String MEDIA_LIST_IMG = "image";

    /* 剧集列表：虾米id（音频使用） */
    public static final String MEDIA_LIST_SOURCEID = "sourceId";

    /* 剧集列表：专辑下详情类型 */
    public static final String MEDIA_LIST_MEDIATYPE = "mediaType";


    /******* 详情 *********/
    /* 剧集详情：视频id */
    public static final String MEDIA_DETAIL_VIDEOID = "videoId";

    /* 剧集详情：标题 */
    public static final String MEDIA_DETAIL_NAME = "name";

    /* 剧集详情：时长 */
    public static final String MEDIA_DETAIL_DURATION = "duration";

    /* 剧集详情：播放地址 */
    public static final String MEDIA_DETAIL_PLAYURL = "playUrl";

    /* 剧集详情：是否外链播放 */
    public static final String MEDIA_DETAIL_PLAYURL_TYPE = "playType";

    /* 剧集详情：页码 */
    public static final String MEDIA_DETAIL_PAGENUM = "pageNum";


    /********  栏目 *********/
    /* 栏目: 列表 */
    public static final String MEDIA_CHANNEL_CHANNELS = "channels";

    /* 栏目: 栏目标题 */
    public static final String MEDIA_CHANNEL_NAME = "name";

    /* 栏目: 栏目地址 */
    public static final String MEDIA_CHANNEL_DATAURL = "dataUrl";

    /* 栏目: cms唯一id */
    public static final String MEDIA_CHANNEL_CMSID = "cmsId";

    /* 栏目: 跳转类型 */
    public static final String MEDIA_CHANNEL_SKIPTYPE = "skipType";

    /* 栏目: 栏目id */
    public static final String MEDIA_CHANNEL_PAGEID = "pageid";

    /************** 搜索 **************/

    /* 专辑Id */
    public static final String MEDIA_SEARCH_VIDEOALBUMS = "videoAlbums";

    /* 专辑Id */
    public static final String MEDIA_SEARCH_AUDIOALBUMS = "audioAlbums";

    /* 专辑Id */
    public static final String MEDIA_SEARCH_AUDIOS = "audios";


    /* 专辑Id */
    public static final String MEDIA_SEARCH_ALBUMID = "albumId";

    /* 评分 */
    public static final String MEDIA_SEARCH_RATING = "rating";

    /* 专辑时长 - 音频 */
    public static final String MEDIA_SEARCH_DURATION = "duration";

    /* 主演  - 视频 */
    public static final String MEDIA_SEARCH_STARRING = "starring";

    /* 导演 - 视频 */
    public static final String MEDIA_SEARCH_DIRECTORY = "directory";

    /* 缩略图 */
    public static final String MEDIA_SEARCH_IMAGES = "images";

    /* 演唱者 - 音频 */
    public static final String MEDIA_SEARCH_SINGER = "singer";

    /* 音频来源 - 音频 */
    public static final String MEDIA_SEARCH_SOURCE = "source";

    /* 音频来源 - 音频 */
    public static final String MEDIA_SEARCH_SOURCENAME = "sourceName";

    /* 音频来源ID - 音频 */
    public static final String MEDIA_SEARCH_SOURCEID = "sourceId";

    /* 音频Id - 音频 */
    public static final String MEDIA_SEARCH_MEDIAID = "mediaId";

    /* 音频Id - 音频 */
    public static final String MEDIA_SEARCH_MEDIATYPE = "mediaType";

}
