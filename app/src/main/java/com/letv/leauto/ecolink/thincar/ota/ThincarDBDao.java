package com.letv.leauto.ecolink.thincar.ota;

import com.letv.leauto.ecolink.thincar.ota.OtaEntity;

import java.util.List;

/**
 * Created by Administrator on 2016/9/30.
 */
public interface ThincarDBDao {

    /**
     * 插入Ota相关信息
     * @param entity
     */
    void insertOtaEntity(OtaEntity entity);

    /**
     * 删除对应车机的ota信息
     * @param carMac
     */
    void deleteOtaEntity(String carMac);

    /**
     * 跟新数据库对应车机信息
     * @param entity
     */
    void updataOtaEntity(OtaEntity entity);

    /**
     * 获取下载的ota
     * @return
     */
    List<OtaEntity> getOtaEntityFromDB();


    /**
     * 根据mac地址查看数据库中是否存在
     * @param carMac
     * @return
     */
    List<OtaEntity> isExists(String carMac);



}
