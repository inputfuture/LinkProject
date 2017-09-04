package com.letv.leauto.ecolink.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.widget.RemoteViews;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;

import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.utils.BitmapUtils;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.Trace;


/**
 * Implementation of App Widget functionality.
 */
public class EcolinkWidget extends AppWidgetProvider {

    public static void updateAppWidget(Context context) {
        AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ecolink_widget);

        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(GlobalCfg.WIDGETYEPE, GlobalCfg.FAVOR);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.favorite, pendingIntent);



        Intent localIntent = new Intent(context, HomeActivity.class);
        localIntent.putExtra(GlobalCfg.WIDGETYEPE, GlobalCfg.LOCAL);
        PendingIntent localPendingIntent = PendingIntent.getActivity(context, 1, localIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.local, localPendingIntent);

        Intent companeyIntent = new Intent(context, HomeActivity.class);
        companeyIntent.putExtra(GlobalCfg.WIDGETYEPE, GlobalCfg.COMPANY);
        PendingIntent companyPendingIntent = PendingIntent.getActivity(context, 2, companeyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.company, companyPendingIntent);

        Intent homeIntent = new Intent(context, HomeActivity.class);
        homeIntent.putExtra(GlobalCfg.WIDGETYEPE, GlobalCfg.HOME);
        PendingIntent homePendingIntent = PendingIntent.getActivity(context, 3, homeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.home, homePendingIntent);

        Intent musicIntent = new Intent(context, HomeActivity.class);
        musicIntent.putExtra(GlobalCfg.WIDGETYEPE, GlobalCfg.MUSIC);
        PendingIntent musicPendingIntent = PendingIntent.getActivity(context, 4, musicIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.img, musicPendingIntent);
        CacheUtils cacheUtil = CacheUtils.getInstance(context);;
        //PlayItem mLastItem = MediaOperation.getInstance().getHistoryLastItem();
        //Trace.Debug("####mLastItem.getTitle="+mLastItem.getTitle()+",mLastItem.getCpName()="+mLastItem.getCpName());
        String album_name = cacheUtil.getString(Constant.Radio.LAST_ALBUM_NAME, null);//
        String music_name = cacheUtil.getString(Constant.Radio.MUSIC_NAME, null);//
        String img_url = cacheUtil.getString(Constant.Radio.LAST_IMG_URL, null);//


        Trace.Debug("####malbum_name="+album_name+",music_name="+music_name+",img_url="+img_url);
//        在这里实现当前播放曲目的文案和图片设置
        if (music_name!= null||album_name!=null){
            views.setTextViewText(R.id.widget_music_warn,"你上次听到这");
            if(album_name==null||album_name.isEmpty()) {
                views.setTextViewText(R.id.album_name, context.getResources().getString(R.string.menu_local_music));
            }else{
                views.setTextViewText(R.id.album_name, album_name);
            }
            views.setTextViewText(R.id.music_name, music_name);
            if(img_url==null||img_url.isEmpty()){
                views.setImageViewBitmap(R.id.img, BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_defult));
            }else {
                Bitmap img = BitmapUtils.returnBitMap(img_url);
                views.setImageViewBitmap(R.id.img, img);
            }
        }else{
            views.setTextViewText(R.id.widget_music_warn,context.getResources().getString(R.string.str_not_listen));
            views.setTextViewText(R.id.album_name,"");
            views.setTextViewText(R.id.music_name,"");
            views.setImageViewBitmap(R.id.img, BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_defult));

        }

     appWidgetManager.updateAppWidget(new ComponentName(context,EcolinkWidget.class), views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
     Trace.Debug("#### onReceive--->"+context.getPackageName());
        updateAppWidget(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateAppWidget(context);
        Trace.Debug("#### onUpdate");
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
        super.onEnabled(context);
    }

}

