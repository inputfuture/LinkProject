package com.letv.leauto.ecolink.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.utils.DensityUtils;
import com.letv.leauto.ecolink.utils.Trace;

/**
 * Created by wang.huayan on 2016/6/20.
 */
public class SlideTablayout extends HorizontalScrollView {
    private static final float TAB_VIEW_TEXT_SIZE_SP = 22; //默认标题的大小
    private static final float TAB_VIEW_PADDING_DIPS = 25;// 默认标题的padding
    private static final int ANCHOR_VIEW_WIDTH = 5; //底部指示器的宽度
    private static final int TAB_VIEW_TEXT_COLOR = Color.WHITE;//默认标题的颜色
    private static final int TAB_VIEW_TEXT_HIGHT_COLOR = Color.BLUE; //选中标题的颜色
    private static final int ANCHOR_VIEW_TOP_MARGIN = 0; //默认 指示器距离标题的高度
    private static final int ANCHOR_VIEW_COLOR = Color.GREEN; //默认指示器的颜色


    private SlideTabStrip mTabStrip;
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private int mScrollState;
    private int mAnchorHeight = ANCHOR_VIEW_WIDTH; //滑动条的高度
    private int mWidth;  //整个view的宽度
    private int mAnchorViewTopMargin = ANCHOR_VIEW_TOP_MARGIN; //滑动条距离上边卡页的距离
    private Context mContext;
    private TextView mAnchorView;
    private LinearLayout mTitleLayout;
    private int mTabTitleColor = TAB_VIEW_TEXT_COLOR; //卡页标题中默认字体颜色
    private int mTabTitleHightColor = TAB_VIEW_TEXT_HIGHT_COLOR; //卡页标题中的高亮颜色
    private int mAnchorColor = ANCHOR_VIEW_COLOR; //滑动条的颜色
    private int mTabViewId;
    private int mAnchorId;
    private int mAnchorLayoutId;
    private int mTabLayoutId;
    private float mTitleTextSize;

    public void setGravityType(SlideTabStrip.GravityType mDefaultGravityType) {
        this.mDefaultGravityType = mDefaultGravityType;
    }

    private SlideTabStrip.GravityType mDefaultGravityType = SlideTabStrip.GravityType.BOTTOM;

    public SlideTablayout(Context context) {
        this(context, null);
    }

    public SlideTablayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public SlideTablayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setFillViewport(true);
        setHorizontalScrollBarEnabled(false);
        mTabStrip = new SlideTabStrip(context);
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        mTabStrip.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//            @Override
//            public void onGlobalLayout() {
//                mWidth = getWidth();
//                if(mViewPager!=null){
//                    setSelection(mViewPager.getCurrentItem());
//                    if (mIsFixed){
//                        for (int i = 0; i < mTitleLayout.getChildCount(); i++) {
//                            LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) mTitleLayout.getChildAt(i).getLayoutParams();
//                            layoutParams.width= (int) getWidth()/mTitleLayout.getChildCount();
//                            mTitleLayout.getChildAt(i).setLayoutParams(layoutParams);
//                        }
//
//                    }
//                }
//                mTabStrip.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//            }
//        });

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int val = MeasureSpec.getSize(heightMeasureSpec);
        int result = 0;
        switch (mode)
        {
            case MeasureSpec.EXACTLY:
                result = val;
                break;
            default:
                int count =getChildCount();
                if (count>0){
                    result=getChildAt(0).getMeasuredHeight();
                }

                break;
        }
        result = mode == MeasureSpec.AT_MOST ? Math.min(result, val) : result;
        return result + getPaddingTop() + getPaddingBottom();
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int val = MeasureSpec.getSize(widthMeasureSpec);
        int result = 0;
        switch (mode)
        {
            case MeasureSpec.EXACTLY:
                result = val;
                break;
            default:
                int count =getChildCount();
                if (count>0){
                    result=getChildAt(0).getMeasuredWidth();
                }
                break;
        }
        result = mode == MeasureSpec.AT_MOST ? Math.min(result, val) : result;
        return result + getPaddingLeft() + getPaddingRight();
    }

    public void setAnchorViewId(int id) {
        mAnchorId = id;


    }

    public void setDiverColor(int color){
        mTabStrip.setDiverColor(color);


    }

    public void setAnchorLayoutId(int id) {
        mAnchorLayoutId = id;

    }

    public void setTabLayoutId(int mTabLayoutId) {
        this.mTabLayoutId = mTabLayoutId;
    }

    public void setTabViewId(int id) {
        mTabViewId = id;


    }

    public int getTabTitleColor() {
        return mTabTitleColor;
    }

    public void setTabTitleColor(int mTabTitleColor) {
        this.mTabTitleColor = mContext.getResources().getColor(mTabTitleColor);
    }

    public int getTabTitleHightColor() {
        return mTabTitleHightColor;
    }

    public void setTabTitleHightColor(int mTabTitleHightColor) {
        this.mTabTitleHightColor = mContext.getResources().getColor(mTabTitleHightColor);
    }
    private  int mFixSize=0;
    private boolean mIsFixed=false;

    public void setFixSize(int size){
        mFixSize=size;

    }
    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        if (mViewPager != null) {
            populayteStrip();
            mViewPager.addOnPageChangeListener(new InternalViewpagerListener());
        }

    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void populayteStrip() {
        if (mViewPager==null){
            return;
        }
        mTabStrip.removeAllViews();
        int width= getLayoutParams().width;
        PagerAdapter pagerAdapter = mViewPager.getAdapter();
        if (mAnchorLayoutId != 0) {
            View view = LayoutInflater.from(mContext).inflate(mAnchorLayoutId, mTabStrip, false);
            mAnchorView = (TextView) view.findViewById(mAnchorId);
        }
        if (mAnchorView == null) {
            mAnchorView = createDefaultAnchorView(mContext);
        }
        final int count=pagerAdapter.getCount();
        if (count<=mFixSize){
            mIsFixed=true;
        }
        mTitleLayout = new LinearLayout(mContext);
        mTitleLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mWidth = getWidth();
                if(mViewPager!=null){
                    if (mIsFixed){
                        int itemWidth =mTitleLayout.getChildAt(0).getWidth();
                        int margin=(mWidth-itemWidth*count)/(2*count);
                        for (int i = 0; i < count; i++) {
                            View view=mTitleLayout.getChildAt(i);
                            LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams)view.getLayoutParams();
                            layoutParams.leftMargin= margin;
                            layoutParams.rightMargin=margin;
                            mTitleLayout.getChildAt(i).setLayoutParams(layoutParams);
                        }
                        RelativeLayout.LayoutParams mAnchorViewLayoutParams = (RelativeLayout.LayoutParams) mAnchorView.getLayoutParams();
                        mAnchorViewLayoutParams.width = itemWidth;
                        mAnchorViewLayoutParams.leftMargin = margin;
                        if (mDefaultGravityType== SlideTabStrip.GravityType.CENTER){
                            mAnchorViewLayoutParams.height=mTitleLayout.getChildAt(0).getHeight();
                        }
                        mAnchorView.setLayoutParams(mAnchorViewLayoutParams);

                    }

                    setSelection(mViewPager.getCurrentItem());
                }
                mTitleLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        });
        RelativeLayout.LayoutParams  titleParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mTitleLayout.setLayoutParams(titleParams);
        mTitleLayout.setGravity(Gravity.CENTER);
        mTitleLayout.setClipChildren(true);
        mTitleLayout.setClipToPadding(true);
        mTabStrip.setTitleLayout(mTitleLayout);
        mTabStrip.setAnchorView(mAnchorView);
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            TextView tabView = null;
            if (mTabLayoutId != 0) {
                View view = LayoutInflater.from(mContext).inflate(mTabLayoutId,mTitleLayout,false);
                tabView = (TextView) view.findViewById(mTabViewId);

            }
            if (tabView == null) {
                tabView = createDefaultTabView(mContext);
            }

            mTitleTextSize =tabView.getPaint().getTextSize();
            tabView.setText(pagerAdapter.getPageTitle(i));
            tabView.setOnClickListener(new TabViewOnclickListener());
            mTitleLayout.addView(tabView);

        }
        if (mDefaultGravityType == SlideTabStrip.GravityType.BOTTOM) {
            mTabStrip.addView(mTitleLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams anchorParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            anchorParams.height = mAnchorHeight;
            anchorParams.topMargin = mAnchorViewTopMargin;
            mAnchorView.setLayoutParams(anchorParams);
            mTitleLayout.setId(R.id.my_view);
            anchorParams.addRule(RelativeLayout.BELOW, mTitleLayout.getId());
            mTabStrip.addView(mAnchorView);
        } else if (mDefaultGravityType == SlideTabStrip.GravityType.CENTER) {
            RelativeLayout.LayoutParams anchorParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            anchorParams.addRule(RelativeLayout.CENTER_VERTICAL);
            mAnchorView.setLayoutParams(anchorParams);
            mTabStrip.addView(mAnchorView);
            mTabStrip.addView(mTitleLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        }


    }

    class TabViewOnclickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (mViewPager==null)
                return;
            for (int i = 0; i < mTitleLayout.getChildCount(); i++) {
                if (v == mTitleLayout.getChildAt(i)) {
                    mViewPager.setCurrentItem(i);
                    ((TextView)mTitleLayout.getChildAt(i)).setTextColor(mTabTitleHightColor);
                }else {
                    ((TextView)mTitleLayout.getChildAt(i)).setTextColor(mTabTitleColor);
                }
            }

        }
    }

    protected TextView createDefaultTabView(Context context) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP);
        textView.setTypeface(Typeface.DEFAULT_BOLD);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                    outValue, true);
            textView.setBackgroundResource(outValue.resourceId);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // If we're running on ICS or newer, enable all-caps to match the Action Bar tab style
            textView.setAllCaps(true);
        }

        int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding, padding, padding);
        return textView;
    }


    protected TextView createDefaultAnchorView(Context context) {
        TextView textView = new TextView(context);
        textView.setBackgroundColor(mAnchorColor);
        return textView;
    }

    public void setViewPagerListener(ViewPager.OnPageChangeListener viewPagerListener) {
        mOnPageChangeListener = viewPagerListener;
    }

    public void setSelection(int position) {
        setSelectionAnchorLayout(position);
        setSelectionTabTextColor(position);


    }

    private void setSelectionAnchorLayout(int position) {


        View view = mTitleLayout.getChildAt(position);
        int left = view.getLeft();
        int right = view.getRight();
        int center=(left+right)/2;
        Trace.Debug("##### getleft="+getLeft()+"   getright ="+getRight());
        int viewCenter=(getLeft()+getRight())/2;
//        if (center)
//        if (right >= mWidth) {
//            scrollTo(right - mWidth, 0);
//        }
        if(right>=viewCenter){
            scrollTo(right-viewCenter,0);
        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAnchorView.getLayoutParams();
        int width = right - left;
        layoutParams.width = width;
        layoutParams.leftMargin = left;
        if (mDefaultGravityType== SlideTabStrip.GravityType.CENTER){
            layoutParams.height=view.getHeight();
        }
        mAnchorView.setLayoutParams(layoutParams);
    }

    private void setSelectionTabTextColor(int position) {
        int childCount = mTitleLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView textView = (TextView) mTitleLayout.getChildAt(i);
            if (position == i) {
                textView.setTextColor(mTabTitleHightColor);
                textView.getPaint().setTextSize((float) (mTitleTextSize*1.2));
//                mTitleLayout.removeView(textView);
//                mTitleLayout.addView(textView,position);
            } else {
                textView.setTextColor(mTabTitleColor);
                textView.getPaint().setTextSize(mTitleTextSize);
//                mTitleLayout.removeView(textView);
//                mTitleLayout.addView(textView,position);

            }
        }
    }

    class InternalViewpagerListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mViewPager.getParent().requestDisallowInterceptTouchEvent(true);
            if (positionOffset >= 0) {
                if (position < mTitleLayout.getChildCount() - 1) {
                    View view = mTitleLayout.getChildAt(position);
                    View nextView = mTitleLayout.getChildAt(position + 1);
                    if (view != null) {
                        int left = view.getLeft();
                        int right = view.getRight();
                        left = (int) ((1 - positionOffset) * left + positionOffset * nextView.getLeft());
                        right = (int) ((1 - positionOffset) * right + positionOffset * nextView.getRight());
                        int center=(left+right)/2;
                        int viewCenter=(getLeft()+getRight())/2;
//                        if (right >= mWidth) {
//                            scrollTo(right - mWidth, 0);
//                        }

                        if (right>=viewCenter){
                            scrollTo(right-viewCenter,0);
                        }
                        if (position == 0) {
                            scrollTo(0, 0);
                        }
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAnchorView.getLayoutParams();
                        int width = right - left;
                        layoutParams.width = width;
                        layoutParams.leftMargin = left;
                        if (mDefaultGravityType== SlideTabStrip.GravityType.CENTER){
                            layoutParams.height=view.getHeight();
                        }
                        mAnchorView.setLayoutParams(layoutParams);


                    }

                }
//                mTabStrip.pagerChange();
            }


            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

        }

        @Override
        public void onPageSelected(int position) {

            setSelectionTabTextColor(position);
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                setSelection(position);
            }

            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(position);
            }
        }


        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }

        }
    }
}
