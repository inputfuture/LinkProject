package com.letv.leauto.ecolink.ui.leradio_interface.data;

import com.letv.leauto.ecolink.database.model.MediaDetail;

import java.util.List;

/**
 * Created by Administrator on 2016/11/3.
 */
public class PositiveSeries {
    private int page;
    private int pageSize;
    private List<MediaDetail> positivieSeries;

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

    public List<MediaDetail> getPositivieSeries() {
        return positivieSeries;
    }

    public void setPositivieSeries(List<MediaDetail> positivieSeries) {
        this.positivieSeries = positivieSeries;
    }
}
