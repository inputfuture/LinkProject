package com.letv.leauto.ecolink.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

/**
 * @ClassName:QuickAlphabeticBar.java
 * @Description:通讯录字母滑动条
 * @author:
 * @date:2013-8-21 下午10:12:19
 */
public class QuickAlphabeticBar extends ImageButton {
    // 用于显示当前选中的字母
    private TextView mDialogText;
    // 接受子线程发送的数据（滑动条的字母）, 并用此数据配合主线程更新UI
    private Handler mHandler;
    // 联系热ListView
    private ListView mList;
    // 滑动子母条的高度
    private float mHight;
    // 需要显示在滑动条的字母
    private String[] letters = new String[]{"A", "B", "C",
            "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
            "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    // 存放联系人汉语拼音首字母和与之对应的列表位置
    private HashMap<String, Integer> alphaIndexer;
    Paint paint = new Paint();
    int choose = -1;

    public QuickAlphabeticBar(Context context) {
        super(context);
    }

    public QuickAlphabeticBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QuickAlphabeticBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @param @param ctx
     * @return void
     * @Title:init
     * @Description:将选中的当前字母显示在TextView中
     */
    public void init(TextView ctx) {
        mDialogText = ctx;
        mDialogText.setVisibility(View.INVISIBLE);
        mHandler = new Handler();
    }

    /**
     * @param @param mList
     * @return void
     * @Title:setListView
     * @Description:设置Listview
     */
    public void setListView(ListView mList) {
        this.mList = mList;
    }

    /**
     * @param @param alphaIndexer
     * @return void
     * @Title:setAlphaIndexer
     * @Description:设置联系人姓名的汉语拼音首字母和与之对应的列表位置
     */
    public void setAlphaIndexer(HashMap<String, Integer> alphaIndexer) {
        this.alphaIndexer = alphaIndexer;
    }

    /**
     * @param @param mHight
     * @return void
     * @Title:setHight
     * @Description:设置滑动子母条的高度
     */
    public void setHight(float mHight) {
        this.mHight = mHight;
    }

    /**
     * 重写onTouchEvent，手指触摸到每个字母时显示对应的联系人信息。
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int act = event.getAction();
        float y = event.getY();
        final int oldChoose = choose;
        // 计算手指位置，找到对应的段，让mList移动段开头的位置
        int selectIndex = (int) (y / (mHight / letters.length));
        if (selectIndex > -1 && selectIndex < letters.length) {// 防止越界
            String key = letters[selectIndex];
            if (alphaIndexer.containsKey(key)) {
                int pos = alphaIndexer.get(key);
                if (mList.getHeaderViewsCount() > 0) {
                    this.mList.setSelectionFromTop(
                            pos + mList.getHeaderViewsCount(), 0);
                } else {
                    this.mList.setSelectionFromTop(pos, 0);
                }

            }

            mDialogText.setText(key);
        }

        switch (act) {
            // 初次触摸
            case MotionEvent.ACTION_DOWN:
                if (oldChoose != selectIndex) {
                    if (selectIndex > 0 && selectIndex < letters.length) {
                        choose = selectIndex;
                        invalidate();
                    }
                }

                if (mHandler != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mDialogText != null
                                    && mDialogText.getVisibility() == View.INVISIBLE) {
                                mDialogText.setVisibility(VISIBLE);
                            }
                        }
                    });
                }
                break;
            // 接触屏幕进行滑动操作
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != selectIndex) {
                    if (selectIndex > 0 && selectIndex < letters.length) {
                        choose = selectIndex;
                        invalidate();
                    }
                }
                break;
            // 触摸完后抬起
            case MotionEvent.ACTION_UP:
                choose = -1;
                if (mHandler != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mDialogText != null
                                    && mDialogText.getVisibility() == View.VISIBLE) {
                                mDialogText.setVisibility(INVISIBLE);
                            }
                        }
                    });
                }
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     *
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#00FFFFFF"));

        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / letters.length;
        for (int i = 0; i < letters.length; i++) {
            paint.setColor(Color.parseColor("#00000000"));
            paint.setTextSize(30);
            paint.setTypeface(Typeface.DEFAULT);
            paint.setAntiAlias(true);
            if (i == choose) {
                paint.setColor(Color.parseColor("#00000000"));// 滑动时按下的字母颜色
                paint.setFakeBoldText(true);
            }
            float xPos = width / 2f - paint.measureText(letters[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(letters[i], xPos, yPos, paint);
            paint.reset();
        }

    }

}
