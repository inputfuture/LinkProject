package com.letv.leauto.ecolink.ui.leradio_interface.parameter;

import com.letv.leauto.ecolink.ui.leradio_interface.baseparameter.HttpBaseParameter;

import java.util.Map;

/**
 * Created by Han on 16/7/15.
 */
public class GetMusicDetailDynamicParameter extends HttpBaseParameter {
    private static final long serialVersionUID = 1L;

    /*detailid */
    private final String KEY_DETAILID = "mediaId";
    /* type */
    private final String KEY_TYPE = "mediaType";
    /* playtype */
    private final String KEY_PLAYTYPE = "playType";

    private String detailId;
    private String type;
    private String playType;

    public GetMusicDetailDynamicParameter(String detailId, String type) {
        this.detailId = detailId;
        this.type = type;
        this.playType = "1";
    }

    public GetMusicDetailDynamicParameter(String detailId, String type, String playType) {
        this.detailId = detailId;
        this.type = type;
        this.playType = playType;
    }

    public void setQueris(Map<String, String> queris) {

    }

    @Override
    public HttpBaseParameter combineParams() {
        super.combineParams();
        this.put(KEY_DETAILID, this.detailId);
        this.put(KEY_TYPE, this.type);
        this.put(KEY_PLAYTYPE, this.playType);
        return this;
    }

}
