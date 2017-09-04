package com.letv.leauto.ecolink.ui.leradio_interface.dataprovider;

public interface DataProviderInterface {

    /**
     * 得到数据了
     * @param data
     *            得到数据了
     * @param dataTime
     *            得到数据的时间
     */
    public void onGetData(Object data, long dataTime, int dataSource,
                          GetDataBackFlags backFlags);

    public void onFailed(GetDataBackFlags backFlags);

    /**
     * 开始从服务器读取数据
     */
    public void onStartGetFromServer();
}
