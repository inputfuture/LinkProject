package com.letv.leauto.ecolink.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yangwei8 on 2016/8/30.
 */
public class DatePickDialog extends Dialog implements View.OnClickListener {
    NumberPicker np_year;
    NumberPicker np_month;
    NumberPicker np_day;
   //ImageView time_choose;
   // ImageView time_cancel;
    private Context mContext;
    private DateConfirmCallback listener;
    private LinearLayout mContentView;
    private int year=0,month=0,day=0;
    public DatePickDialog(Context context, int theme,String message) {
        super(context,  theme);
        mContext = context;
        init();
    }
    public interface DateConfirmCallback {
        void onConfirmClick(DatePickDialog datePickDialog);
    }
    private void init(){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (GlobalCfg.IS_POTRAIT) {
            mContentView = ( LinearLayout) inflater.inflate(R.layout.date_pick_p, null);
        }else{
            mContentView = ( LinearLayout) inflater.inflate(R.layout.date_pick, null);
        }

        setContentView(mContentView);
       // time_choose= (ImageView) findViewById(R.id.time_choose);
       // time_cancel= (ImageView) findViewById(R.id.time_cancel);

        np_year= (NumberPicker) findViewById(R.id.np_year);
        np_month= (NumberPicker) findViewById(R.id.np_month);
        np_day= (NumberPicker) findViewById(R.id.np_day);
        np_year.setOnClickListener(this);
        np_month.setOnClickListener(this);
        np_day.setOnClickListener(this);
        initDate();
        np_year.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                year=newVal;
            }
        });
        np_month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                month=newVal;
            }
        });
        np_day.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                day=newVal;
            }
        });

    }
    private void initDate(){
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH)+1;
        day = c.get(Calendar.DAY_OF_MONTH);
        np_year.setMinValue(2000);
        np_month.setMinValue(1);
        np_day.setMinValue(1);
        np_year.setMaxValue(year);
        np_month.setMaxValue(12);
        np_day.setMaxValue(31);
        np_year.setValue(year);
        np_month.setValue(month);
        np_day.setValue(day);
    }
    public void setListener(DateConfirmCallback  listener) {
        this.listener = listener;
    }
    @Override
    public void onClick(View v) {
                if (listener!=null){
                    listener.onConfirmClick(this);
                    dismiss();
                }
    }
    public String getDate(){
        return year+"-"+month+"-"+day;
    }
}
