package com.letv.leauto.ecolink.utils;

/**
 * Created by yangwei8 on 2016/9/8.
 */
public  class VehicleConst {
   // public static String vehicleUrl="http://api.leautolink.com/hz/";
   public static String vehicleUrl="https://api.leautolink.com/hz/";//线上环境
//    public static String vehicleUrl="http://id-bj.ffauto.us/hz/";//测试环境
   // public static String vehicleUrl="http://10.58.185.183:9000/hz/";
    public static String qauthUrl="https://api.leautolink.com/ffid/";//线上环境
//    public static String qauthUrl="http://id-bj.ffauto.us/ffid/";//测试环境
   // public static String qauthUrl="http://10.58.185.183:9000/ffid/";
    public static String testToken;
    public static String replaceNum(String str){
        str = str.replace("+86","");
        return str;
    }
}
