package com.letv.leauto.ecolink.cfg;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.amap.api.services.core.LatLonPoint;

/**
 * Created by zhaochao on 2015/9/1.
 */
public class EnvStatus {
    private static final String STATUS_SP_FILE = "status_sp_file";

    //not need save to sp
    public static boolean isLinked;
    public static float screenLight;

    //need save to sp
    public static int Sort_Index;
    public static String Sort_Id;
    public static String Sort_Type;
    public static int Album_Index;
    public static String Album_Id;
    public static String Album_Type_Id;
    public static LatLonPoint mypoint;
    public static void read(Context ctx) {
        try{
            SharedPreferences sp = ctx.getSharedPreferences(STATUS_SP_FILE, Activity.MODE_PRIVATE);
            Sort_Index = sp.getInt("Sort_Index", 0);
            Sort_Id = sp.getString("Sort_Id", "NONE");
            Sort_Type = sp.getString("Sort_Type", "NONE");
            Album_Index = sp.getInt("Album_Index", 0);
            Album_Id = sp.getString("Album_Id", "NONE");
            Album_Type_Id = sp.getString("Album_Type_Id", "NONE");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save(Context ctx) {
        try{
            SharedPreferences sp = ctx.getSharedPreferences(STATUS_SP_FILE, Activity.MODE_PRIVATE);
            sp.edit().putInt("Sort_Index", Sort_Index)
                    .putString("Sort_Id", Sort_Id)
                    .putString("Sort_Type", Sort_Type)
                    .putInt("Album_Index", Album_Index)
                    .putString("Album_Id", Album_Id)
                    .putString("Album_Type_Id", Album_Type_Id)
                    .apply();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void savePoi(LatLonPoint point){
        mypoint = point;
    }

    public static LatLonPoint getPoi(){
        return mypoint ;
    }
}
