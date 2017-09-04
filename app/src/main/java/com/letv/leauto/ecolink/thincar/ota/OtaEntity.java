package com.letv.leauto.ecolink.thincar.ota;

/**
 * Created by Administrator on 2016/9/30.
 */
public class OtaEntity {

    String carMac;//车的唯一标识
    String carVersion;//车的版本
    String carModle;//车的型号
    int downStatus;//文件下载状态
    int unzipStatus;//文件解压状态
    String md5;//网络返回的效验值
    String filePath;//文件下载路径
    String fileName;//文件名字
    String downUrl;//文件下载路径
    String message;//更新信息
    String time;//升级包时间

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    int progress;
    int pkgSize;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public OtaEntity() {
    }

    public OtaEntity(String carMac, String carVersion, String carModle, int downStatus, int unzipStatus, String md5, String filePath, String fileName, String downUrl, int pkgSize) {
        this.carMac = carMac;
        this.carVersion = carVersion;
        this.carModle = carModle;
        this.downStatus = downStatus;
        this.unzipStatus = unzipStatus;
        this.md5 = md5;
        this.filePath = filePath;
        this.fileName = fileName;
        this.downUrl = downUrl;
        this.pkgSize = pkgSize;
    }

    public String getCarMac() {
        return carMac;
    }

    public void setCarMac(String carMac) {
        this.carMac = carMac;
    }

    public String getCarVersion() {
        return carVersion;
    }

    public void setCarVersion(String carVersion) {
        this.carVersion = carVersion;
    }

    public String getCarModle() {
        return carModle;
    }

    public void setCarModle(String carModle) {
        this.carModle = carModle;
    }

    public int getDownStatus() {
        return downStatus;
    }

    public void setDownStatus(int downStatus) {
        this.downStatus = downStatus;
    }

    public int getUnzipStatus() {
        return unzipStatus;
    }

    public void setUnzipStatus(int unzipStatus) {
        this.unzipStatus = unzipStatus;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public int getPkgSize() {
        return pkgSize;
    }

    public void setPkgSize(int pkgSize) {
        this.pkgSize = pkgSize;
    }

    @Override
    public String toString() {
        return "OtaEntity{" +
                "carMac='" + carMac + '\'' +
                ", carVersion='" + carVersion + '\'' +
                ", carModle='" + carModle + '\'' +
                ", downStatus=" + downStatus +
                ", unzipStatus=" + unzipStatus +
                ", md5='" + md5 + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", downUrl='" + downUrl + '\'' +
                ", message='" + message + '\'' +
                ", time='" + time + '\'' +
                ", progress=" + progress +
                ", pkgSize=" + pkgSize +
                '}';
    }
}
