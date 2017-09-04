package com.letv.leauto.ecolink.utils;

import android.util.Log;

import com.letv.leauto.ecolink.BuildConfig;
import com.tencent.bugly.crashreport.BuglyLog;

public class Trace {
	private static final String Defualt_TAG = "bee";
	private static String mTAG = Defualt_TAG;
	private static boolean releaseDebug = false;
	private static boolean flag = (BuildConfig.DEBUG ||releaseDebug) ;
	private static void SetTag(String tag){
		mTAG = tag;
	}
	/**
	 * 调试打印
	 * @param msg
	 */
	public static void Debug(String msg){
		if(flag){
			Log.d(Defualt_TAG, getWrapperMsg(msg));
		}
		BuglyLog.d(Defualt_TAG, getWrapperMsg(msg));
	}
	/**
	 * 调试打印
	 * @param msg
	 */
	public static void Debug(String tag,String msg){
		SetTag(tag);
		if(flag){
			Log.d(mTAG, getWrapperMsg(msg));
		}
		BuglyLog.d(mTAG, getWrapperMsg(msg));
	}

	/**
	 * 信息打印
	 * @param msg
	 */
	public static void Info(String msg){

		if(flag){
			Log.i(Defualt_TAG, getWrapperMsg(msg));
		}
		BuglyLog.i(Defualt_TAG, getWrapperMsg(msg));
	}

	/**
	 * 信息打印
	 * @param msg
	 */
	public static void Info(String tag,String msg){
		SetTag(tag);
		if(flag){
			Log.i(mTAG, getWrapperMsg(msg));
		}
		BuglyLog.i(mTAG, getWrapperMsg(msg));
	}

	/**
	 * 警告打印
	 * @param msg
	 */
	public static void Warn(String msg){
		Log.w(Defualt_TAG, getWrapperMsg(msg));
		BuglyLog.w(Defualt_TAG, getWrapperMsg(msg));
	}

	/**
	 * 警告打印
	 * @param msg
	 */
	public static void Warn(String tag,String msg){
		SetTag(tag);
		Log.w(mTAG, getWrapperMsg(msg));
		BuglyLog.w(mTAG, getWrapperMsg(msg));
	}

	/**
	 * 错误打印
	 * @param msg
	 */
	public static void Error(String msg){
		Log.e(Defualt_TAG, getWrapperMsg(msg));
		BuglyLog.e(Defualt_TAG, getWrapperMsg(msg));
	}

	/**
	 * 错误打印
	 * @param msg
	 */
	public static void Error(String tag,String msg){
		SetTag(tag);
		Log.e(mTAG, getWrapperMsg(msg));
		BuglyLog.e(mTAG, getWrapperMsg(msg));
	}

	/**
	 * 获取堆栈信息
	 * @param msg
	 */
	private static String getWrapperMsg(String msg){
		StackTraceElement[] stacks = (new Throwable()).getStackTrace();
		String wrapperMsg = "[" + stacks[2].getFileName() +
				":" +
				stacks[2].getLineNumber() + "]: " +
				msg;
		return wrapperMsg;
	}
}
