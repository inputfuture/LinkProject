package com.letv.leauto.ecolink.ui.vehiclebean;

/**
 * Created by yangwei8 on 2016/9/18.
 */
public class MyVehicle {
    private String id;
    private String owner;
    private String vin;
    private String image;
    private String type;
    private String receipt;
    private String receiptUrl;
    private String plate;
    private Boolean used;
    private String purchaseDate;
    private String firstRegistrationPlate;
    private String lastBuyInsurance;
    private String lastMaintenance;
    private String custom;
    private static MyVehicle myVehicle;
    private String sModelId;

    public String getsModelId() {
        return sModelId;
    }

    public void setsModelId(String sModelId) {
        this.sModelId = sModelId;
    }

    public static MyVehicle getInstance() {
        synchronized (MyVehicle.class) {
            if (myVehicle == null) {
                myVehicle = new MyVehicle();
            }
        }
        System.out.println(myVehicle.toString());
        return myVehicle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getVin() {
        if (vin == null) {
            return "";
        }

        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReceipt() {
    //修改bug3987
        if (receipt == null || (receipt != null && receipt.equals("请输入手机号") || receipt.equals("null"))) {
            return "";
        }
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getReceiptUrl() {
        if (receiptUrl == null || receiptUrl.equals("null")) {
            return "";
        }
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public String getPurchaseDate() {
        if (purchaseDate == null || purchaseDate.equals("null")) {
            return "";
        }
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getFirstRegistrationPlate() {

        if (firstRegistrationPlate == null || firstRegistrationPlate.equals("null")) {
            return "";
        }
        return firstRegistrationPlate;
    }

    public void setFirstRegistrationPlate(String firstRegistrationPlate) {
        this.firstRegistrationPlate = firstRegistrationPlate;
    }

    public String getLastBuyInsurance() {
        if (lastBuyInsurance == null || lastBuyInsurance.equals("null")) {
            return "";
        }

        return lastBuyInsurance;
    }

    public void setLastBuyInsurance(String lastBuyInsurance) {
        this.lastBuyInsurance = lastBuyInsurance;
    }

    public String getLastMaintenance() {
        if (lastMaintenance == null || lastMaintenance.equals("null")) {
            return "";
        }

        return lastMaintenance;
    }

    public void setLastMaintenance(String lastMaintenance) {
        this.lastMaintenance = lastMaintenance;
    }

    public String getCustom() {
        if (custom == null || custom.equals("null")) {
            return "";
        }

        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    @Override
    public String toString() {
        return "MyVehicle{" +
                "id='" + id + '\'' +
                ", owner='" + owner + '\'' +
                ", vin='" + vin + '\'' +
                ", image='" + image + '\'' +
                ", type='" + type + '\'' +
                ", receipt='" + receipt + '\'' +
                ", receiptUrl='" + receiptUrl + '\'' +
                ", plate='" + plate + '\'' +
                ", used=" + used +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", firstRegistrationPlate='" + firstRegistrationPlate + '\'' +
                ", lastBuyInsurance='" + lastBuyInsurance + '\'' +
                ", lastMaintenance='" + lastMaintenance + '\'' +
                ", custom='" + custom + '\'' +
                ", sModelId='" + sModelId + '\'' +
                '}';
    }
}
