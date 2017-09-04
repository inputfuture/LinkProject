package com.letv.leauto.ecolink.ui.vehiclebean;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

/**
 * Created by yangwei8 on 2016/9/8.
 */
public class VehicleType {
    private ArrayList<String> type;
    private ArrayList<String> id;
    private ArrayList<String> url;
    private ArrayList<String> masterId;
    private ArrayList<String> name;
    private ArrayList<String> brandName;
    private ArrayList<String> serialName;
    private ArrayList<String> yearType;
    private ArrayList<String> fullName;
    private ArrayList<String> sModelId;
    private ArrayList<String> sSerialId;
    public ArrayList<String> getsSerialId() {return sSerialId;}
    public void setsSerialId(ArrayList<String> sSerialId) {this.sSerialId = sSerialId;}
    public void addsSerialId(String s){
        sSerialId.add(s);
    }

    public ArrayList<String> getType() {
        return type;
    }
    public void setType(ArrayList<String> type) {
        this.type = type;
    }
    public void addType(String s){
        type.add(s);
    }

    public ArrayList<String> getId(){return id;}
    public void setId(ArrayList<String> id){this.id=id;}
    public void addId(String s){id.add(s);}

    public ArrayList<String> getUrl(){return url;}
    public void setUrl(ArrayList<String> url){this.url=url;}
    public void addUrl(String s){url.add(s);}

    public ArrayList<String> getMasterId(){return masterId;}
    public void setMasterId(ArrayList<String> masterId){this.masterId=masterId;}
    public void addMasterId(String s){masterId.add(s);}

    public ArrayList<String> getName(){return name;}
    public void setName(ArrayList<String> name){this.name=name;}
    public void addName(String s){name.add(s);}

    public ArrayList<String> getBrandName(){return brandName;}
    public void setBrandName(ArrayList<String> brandName){this.brandName=brandName;}
    public void addBrandName(String s){brandName.add(s);}

    public ArrayList<String> getSerialName(){return serialName;}
    public void setSerialName(ArrayList<String> serialName){this.serialName=serialName;}
    public void addSerialName(String s){serialName.add(s);}

    public ArrayList<String> getYearType(){return yearType;}
    public void setYearType(ArrayList<String> yearType){this.yearType=yearType;}
    public void addYearType(String s){yearType.add(s);}

    public ArrayList<String> getFullName(){return fullName;}
    public void setFullName(ArrayList<String> fullName){this.fullName=fullName;}
    public void addFullName(String s){fullName.add(s);}

    public ArrayList<String> getSModelId(){return sModelId;}
    public void setSModelId(ArrayList<String> sModelId){this.sModelId=sModelId;}
    public void addSModelId(String s){sModelId.add(s);}

}
