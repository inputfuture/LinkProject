package com.letv.leauto.ecolink.ui.leradio_interface.data;

import com.letv.mobile.http.model.LetvHttpBaseModel;

/**
 * Created by Han on 16/7/16.
 */
public class VideoListSeriesModel extends LetvHttpBaseModel {

    private int page;
    private int pageSize;
    private VideoListModel[] positiveSeries;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public VideoListModel[] getPositiveSeries() {
        return positiveSeries;
    }

    public void setPositiveSeries(VideoListModel[] positiveSeries) {
        this.positiveSeries = positiveSeries;
    }
}
