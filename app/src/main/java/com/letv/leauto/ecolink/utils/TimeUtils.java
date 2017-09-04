package com.letv.leauto.ecolink.utils;

import android.content.Context;

import com.letv.leauto.ecolink.EcoApplication;
import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {
    public static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat sdfH = new SimpleDateFormat("HH");
    public static SimpleDateFormat sdfM = new SimpleDateFormat("MM");
    public static final int WEEKDAYS = 7;
    //24 hours  x 60 minutes x 60 seconds = 86400 seconds = 86400,000 milliseconds
    public static final int OneDayTime = 86400000;
    public static final long ONE_MINUTE_TIEM = 60 * 1000;
    public static String[] WEEK = {
            EcoApplication.getInstance().getString(R.string.str_sundays),
            EcoApplication.getInstance().getString(R.string.str_monday),
            EcoApplication.getInstance().getString(R.string.str_tuesday),
            EcoApplication.getInstance().getString(R.string.str_wednesday),
            EcoApplication.getInstance().getString(R.string.str_tuesday),
            EcoApplication.getInstance().getString(R.string.str_friday),
            EcoApplication.getInstance().getString(R.string.str_saturday),
    };

    public static String secToTime(String mediatime) {
        int time = Integer.parseInt(mediatime);
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    /**
     * 初始化时间
     */
    private void initDate() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = EcoApplication.getInstance().getString(R.string.str_sundays);
        } else if ("2".equals(mWay)) {
            mWay = EcoApplication.getInstance().getString(R.string.str_monday);
        } else if ("3".equals(mWay)) {
            mWay = EcoApplication.getInstance().getString(R.string.str_tuesday);
        } else if ("4".equals(mWay)) {
            mWay = EcoApplication.getInstance().getString(R.string.str_wednesday);
        } else if ("5".equals(mWay)) {
            mWay = EcoApplication.getInstance().getString(R.string.str_tuesday);
        } else if ("6".equals(mWay)) {
            mWay = EcoApplication.getInstance().getString(R.string.str_friday);
        } else if ("7".equals(mWay)) {
            mWay = EcoApplication.getInstance().getString(R.string.str_saturday);
        }
        String date = mYear + "." + mMonth + "." + mDay + "" +  mWay;
        // tv_date.setText(date);
    }
    /**
     * 获得年月日星期
     */
    public static String getCurrentData() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = EcoApplication.getInstance().getString(R.string.str_sundays);
        } else if ("2".equals(mWay)) {
            mWay = EcoApplication.getInstance().getString(R.string.str_monday);
        } else if ("3".equals(mWay)) {
            mWay = EcoApplication.getInstance().getString(R.string.str_tuesday);
        } else if ("4".equals(mWay)) {
            mWay = EcoApplication.getInstance().getString(R.string.str_wednesday);
        } else if ("5".equals(mWay)) {
            mWay = EcoApplication.getInstance().getString(R.string.str_tuesday);
        } else if ("6".equals(mWay)) {
            mWay = EcoApplication.getInstance().getString(R.string.str_friday);
        } else if ("7".equals(mWay)) {
            mWay = EcoApplication.getInstance().getString(R.string.str_saturday);
        }
        String date =/* mYear + "年" + */mMonth + "月" + mDay + "日" + mWay;
        return date;
    }
    /*
    * 获取时分
    * */
    public static String getHourMin(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("HH");
        time = new SimpleDateFormat("HH:mm");
        String formattedDate = time.format(c.getTime());
        return formattedDate;
    }

    public static  int getHour(){
        return  Integer.parseInt(sdfH.format(System.currentTimeMillis()));
    }

    public static int getMonth(){
        return Integer.parseInt(sdfM.format(System.currentTimeMillis()));
    }




    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static Long getCurrantTime() {
        Calendar c = Calendar.getInstance();
        Long currantTime = c.getTimeInMillis();
        return currantTime;
    }

    public static Boolean isToday(Date time) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat currantTime = new SimpleDateFormat("HH");
        currantTime = new SimpleDateFormat("yy/MM/dd");
        String formattedDate = currantTime.format(c.getTime());
        String formatCallDate = currantTime.format(time);
        Trace.Info("calllog", "formattedDate=" + formattedDate + ",formatCallDate=" + formatCallDate);
        if (formattedDate.equals(formatCallDate)) {
            return true;
        }
        return false;
    }

    public static Boolean isYesterday(long time) {
        Long thisWeekMinMillis = getInputdayMinTimeMillis( 1);
        Long thisWeekMaxMillis = getInputdayMaxTimeMillis(1);
        Trace.Info("calllog", "thisWeekMinMillis=" + thisWeekMinMillis+",thisWeekMaxMillis=" +thisWeekMaxMillis+",time="+time);
        if (time >= thisWeekMinMillis &&
                time <= thisWeekMaxMillis) {
            return true;
        }
        return false;
    }


    public static Boolean isThisWeek(Long time) {
        int weekday = getWeekAndDay();
        Trace.Info("calllog", "weekday=" + weekday );
        Long thisWeekMinMillis = getInputdayMinTimeMillis(weekday-1);
        Long thisWeekMaxMillis = getInputdayMaxTimeMillis(1);
        Trace.Info("calllog", "thisWeekMinMillis=" + thisWeekMinMillis+",thisWeekMaxMillis=" +thisWeekMaxMillis+",time="+time);
        if (time >= thisWeekMinMillis &&
                time <= thisWeekMaxMillis) {
            return true;
        }
        return false;

    }

    public static Boolean isInOneWeek(Long time) {
        //获取一周内的最大值
        Long thisWeekMaxMillis = getInputdayMinTimeMillis(1);
        //获取一周内的最小值
        Long thisWeekMinMillis = thisWeekMaxMillis - 5 * OneDayTime;
        Trace.Info("calllog", "thisWeekMinMillis=" + thisWeekMinMillis+",thisWeekMaxMillis=" +thisWeekMaxMillis+",time="+time);
        if (time >= thisWeekMinMillis &&
                time <= thisWeekMaxMillis) {
            return true;
        }
        return false;

    }

    private  static int getWeekAndDay() {
        Calendar calendar = Calendar.getInstance();
        //获取当前时间为本月的第几周
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        //获取当前时间为本周的第几天
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        Trace.Info("calllog", "今天是本月的第" + week + "周" + ",星期" + (day-1));
        return (day-1);
    }

    /**
     * 日期变量转成对应的星期字符串
     *
     * @param date
     * @return
     */
    public static String DateToWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayIndex < 1 || dayIndex > WEEKDAYS) {
            return null;
        }
        Trace.Info("calllog", "dayIndex=" + dayIndex);
        return WEEK[dayIndex - 1];
    }


    //获取昨天时间的最小值：

    public static long getInputdayMinTimeMillis(int inPutDay) {
        Calendar mCalendar = Calendar.getInstance();
        long currTime = getCurrantTime();
        mCalendar.setTime(new Date(currTime));

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        mCalendar.set(year, month, day, 0, 0, 0);
        long minToday = mCalendar.getTimeInMillis() - inPutDay * OneDayTime;

        return minToday;
    }

    //获取昨天时间的最大值：
    public static long getInputdayMaxTimeMillis(int inPutDay) {
        Calendar mCalendar = Calendar.getInstance();
        long currTime = getCurrantTime();
        mCalendar.setTime(new Date(currTime));

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        mCalendar.set(year, month, day, 23, 59, 59);
        long minToday = mCalendar.getTimeInMillis() - inPutDay * OneDayTime;

        return minToday;
    }
    /**
     * 防止快速点击
     */
    private static long lastClickTime;
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
    public static boolean isSameDay(Date date, Date sameDate) {

        if (null == date || null == sameDate) {

            return false;

        }

        Calendar nowCalendar = Calendar.getInstance();

        nowCalendar.setTime(sameDate);

        Calendar dateCalendar = Calendar.getInstance();

        dateCalendar.setTime(date);

        if (nowCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR)

                && nowCalendar.get(Calendar.MONTH) == dateCalendar.get(Calendar.MONTH)

                && nowCalendar.get(Calendar.DATE) == dateCalendar.get(Calendar.DATE)) {

            return true;

        }

        // if (date.getYear() == sameDate.getYear() && date.getMonth() == sameDate.getMonth()

        // && date.getDate() == sameDate.getDate()) {

        // return true;

        // }

        return false;

    }
    //获取下载速度
    public static String getSize(long startTime,long endTime,long currentSize){
        java.text.DecimalFormat df=new   java.text.DecimalFormat("#.##");
        String speed;
        long  usedTime = endTime-startTime;
        if (usedTime<=0){
            speed=1+"MB";
        }else {
        if (Double.parseDouble(df.format((currentSize*1000)/((double)usedTime*1024)))<1024){
            speed=df.format((currentSize*1000)/((double)usedTime*1024))+"KB";
        }else {
            speed=df.format((currentSize*1000)/((double)usedTime*1024*1024))+"MB";
        }}
        Trace.Error("==speed==",speed+"=currentSize="+currentSize+"=usedTime="+usedTime);
        return speed;
    }
    //获取加载进度
    public static String getProgress(long size){
        String progress;
        java.text.DecimalFormat df=new  java.text.DecimalFormat("#.##");
        progress=df.format(size/(double)(1024*1024))+"MB";
        Trace.Error("==progress==",progress+"=size="+size);
        return progress;
    }
    //判断是否小于两个小时
    public static boolean getTime(long time){
        Trace.Error("======gettime=",System.currentTimeMillis()+"="+time);
        if ((System.currentTimeMillis()-time)<1.8*60*60*1000){
            return false;//小于两小时
        }else {
            return true;//大于两小时
        }
    }



    public  static  boolean isDayTime(Context context){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String sunrise=CacheUtils.getInstance(context).getString(Constant.SUNRISE,"06:00");
        String sunset=CacheUtils.getInstance(context).getString(Constant.SUNSET,"18:00");
        String hour= sdf.format(new Date());
        if (hour .compareTo(sunrise)>0&&hour.compareTo(sunset)<0){
            return true;
        }
        return false;

    }
}
