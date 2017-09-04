package com.letv.leauto.ecolink.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


import com.leauto.link.lightcar.LogUtils;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.receiver.ScreenRotationUtil;
import com.letv.leauto.ecolink.service.HomeKeyService;
import com.letv.leauto.ecolink.service.ScreenRotationService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2016/9/9.
 */
public class Utils {
    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    public static boolean fileIfExist(String path){
        File testFile = new File(path);
        if(testFile .exists()){
            Trace.Error("==","已存在");
            return true;

        }else {
            Trace.Error("==","不存在");
            return false;
        }
    }

    //使用Bitmap加Matrix来缩放
    public static Bitmap resizeImage(Bitmap bitmap, int reqWidht, int reqHeight) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = reqWidht;
        int newHeight = reqHeight;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    public static byte[] convertFileToBytes(File file) {
        long filesize = file.length();
        if (filesize > Integer.MAX_VALUE) {
            LogUtils.i("Utils", "convertFileToBytes,filesize beyond MAX_VALUE filesize:" + filesize);
            return null;
        }

        byte[] buffer = new byte[(int) filesize];
        FileInputStream fi = null;
        try {
            fi = new FileInputStream(file);
            int offset = 0;
            int numread = 0;
            while (offset < buffer.length && (numread = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numread;
            }

            // 确保所有数据均被读取
            if (offset != buffer.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        } finally {
            if (fi != null) {
                try {
                    fi.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return buffer;
    }

    public static Bitmap convertDrawableToBitMap(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        return bitmap;
    }

    public static byte[] toByteArray(InputStream input)
            throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static int copy(InputStream input, OutputStream output)
            throws IOException {
        long count = copyLarge(input, output);
        if (count > 2147483647L) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static void startGaoDeApp(Context context) {
        if (PackageUtil.ApkIsInstall(context, Constant.GAODE_MAP_PACKAGE_NAME)) {
            context.startActivity(context.getPackageManager().getLaunchIntentForPackage(Constant.GAODE_MAP_PACKAGE_NAME));
            if (!GlobalCfg.IS_POTRAIT && GlobalCfg.CAR_IS_lAND) {
                ScreenRotationUtil.startLandService(context,Constant.GAODE_MAP_PACKAGE_NAME);
            }
        } else {
            ToastUtil.showShort(context, "未安装高德地图");
        }
    }

    public static void startBaidu(Context context){
        if (PackageUtil.ApkIsInstall(context, Constant.BAIDU_MAP_PACKAGE_NAME)) {
            context.startActivity(context.getPackageManager().getLaunchIntentForPackage(Constant.BAIDU_MAP_PACKAGE_NAME));
            if (!GlobalCfg.IS_POTRAIT && GlobalCfg.CAR_IS_lAND) {
                ScreenRotationUtil.startLandService(context,Constant.BAIDU_MAP_PACKAGE_NAME);
            }

        } else {
            ToastUtil.showShort(context, "未安装百度地图");
        }
    }





    /**
     * 判断当前是否回到桌面
     * @param context
     * @return
     */
    public static boolean isCurrentHome(Context context) {
        String topPackage = getTopActivityPackageName(context);
        List<String> homes = getHomes(context);

        if (homes == null || homes.size() <= 0) {
            return false;
        }

        boolean result = false;
        for (String str : homes) {
            if (str.contains(topPackage)) {
                result = true;
                break;
            }
        }

        return result;
    }

    /**
     * 获取最上应用的包名
     * @param context
     * @return
     */
    public static String getTopActivityPackageName(Context context) {
        String topActivityPackage = null;
        ActivityManager activityManager = (ActivityManager) (context
                .getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager
                .getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            topActivityPackage = f.getPackageName();
        }
        return topActivityPackage;
    }

    /**
     * 获得属于桌面的应用的应用包名称
     * @return 返回包含所有包名的字符串列表
     */
    public static List<String> getHomes(Context context) {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = context.getPackageManager();
        //属性
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for(ResolveInfo ri : resolveInfo){
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

    public static String getCurrentTime(int leaveSecond) {
        long time = System.currentTimeMillis();
        time = time + leaveSecond * 1000;
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int min = mCalendar.get(Calendar.MINUTE);
        StringBuffer sb = new StringBuffer();
        if (hour < 10) {
            sb.append("0" + hour);
        } else {
            ;
            sb.append(hour + "");
        }
        sb.append(":");

        if (min < 10) {
            sb.append("0" + min);
        } else {
            sb.append(min + "");
        }

        return sb.toString();
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 原始图片的宽高
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // 在保证解析出的bitmap宽高分别大于目标尺寸宽高的前提下，取可能的inSampleSize的最大值
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // 首先设置 inJustDecodeBounds=true 来获取图片尺寸
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // 计算 inSampleSize 的值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 根据计算出的 inSampleSize 来解码图片生成Bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * 将给定图片维持宽高比缩放后，截取正中间的正方形部分。
     * @param bitmap      原图
     * @param edgeLength  希望得到的正方形部分的边长
     * @return  缩放截取正中部分后的位图。
     */
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength)
    {
        if(null == bitmap || edgeLength <= 0)
        {
            return  null;
        }

        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        if(widthOrg > edgeLength && heightOrg > edgeLength)
        {
            //压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = (int)(edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;

            try{
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            }
            catch(Exception e){
                return null;
            }

            //从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;

            try{
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
                scaledBitmap.recycle();
            }
            catch(Exception e){
                return null;
            }
        }

        return result;
    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return
     */
    public static void copyFile(final String oldPath, final String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                fs.flush();
                fs.close();
                inStream.close();
                oldfile.delete();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    public static boolean IsLocalExiste(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return true;
        }
        return false;
    }
}
