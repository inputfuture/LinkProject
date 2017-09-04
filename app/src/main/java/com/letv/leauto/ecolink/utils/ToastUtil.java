package com.letv.leauto.ecolink.utils;

import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.leauto.ecolink.R;

public class ToastUtil {

//	public static void show(Context context, String info) {
//		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
//	}
//
//	public static void show(Context context, int info) {
//		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
//	}

	private static Toast toast;

	public static void show(Context context, CharSequence text) {


		if (toast == null) {
			toast=new Toast(context.getApplicationContext());
		}

		View view= LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.toast_layout, null);
		TextView tv=(TextView)view.findViewById(R.id.tv_toast_content);

		tv.setText(text);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(view);
//		toast.setGravity(Gravity.CLIP_VERTICAL, 0, 0);
		toast.show();
	}
	public static void showShort(Context context, CharSequence text) {

		if (toast == null) {
			toast=new Toast(context);
		}

		View view= LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
		TextView tv=(TextView)view.findViewById(R.id.tv_toast_content);

		tv.setText(text);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	public static void show(Context context, int text) {

		if (toast == null) {
			toast=new Toast(context);
		}

		View view= LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
		TextView tv=(TextView)view.findViewById(R.id.tv_toast_content);

		tv.setText(context.getResources().getString(text));
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(view);
//		toast.setGravity(Gravity.CLIP_VERTICAL, 0, 0);
		toast.show();
	}
}
