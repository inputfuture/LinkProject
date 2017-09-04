package com.letv.leauto.ecolink.ui.leradio_interface.parameter;

import com.letv.leauto.ecolink.ui.leradio_interface.baseparameter.HttpBaseParameter;
import com.letv.mobile.http.parameter.LetvBaseParameter;

/**
 * Created by Han on 16/7/16.
 */
public class VideoListParameter extends HttpBaseParameter {

    private final String KEY_AID = "albumId";
    private final String KEY_PAGE = "page";
    private final String KEY_TYPE = "mediaType";

    String albumid;
    String page;
    String type;

    public VideoListParameter(String albumid, String type, String page) {
        this.albumid = albumid;
        this.page = page;
        this.type = type;
    }

    @Override
    public HttpBaseParameter combineParams() {
        super.combineParams();
        this.put(this.KEY_AID, this.albumid);
        this.put(this.KEY_PAGE, this.page);
        this.put(this.KEY_TYPE, this.type);

        return this;
    }

}
