package com.letv.leauto.ecolink.ui.view;

import android.view.MotionEvent;

public interface LeGestureListener {
	public void onClick(MotionEvent e);
	public void onLongClick(MotionEvent e);
	/**
	 * 手势滑动时别调用 e1： The first down motion event that started the fling.手势起点的移动事件
	 * e2： The move motion event that triggered the mCurrentIndex onFling.当前手势点的移动事件
	 * velocityX： The velocity of this fling measured in pixels per second along
	 * the x axis.每秒x轴方向移动的像素 velocityY： The velocity of this fling measured in
	 * pixels per second along the y axis.每秒y轴方向移动的像素
	 */
	public void onFling(int orientation, MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY);


}
