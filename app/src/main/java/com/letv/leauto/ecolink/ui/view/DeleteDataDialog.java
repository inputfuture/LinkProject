package com.letv.leauto.ecolink.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.SettingCfg;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.utils.CacheUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DeleteDataDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    @Bind(R.id.tv_download)
     TextView mTvDownload;
    @Bind(R.id.tv_cancle)
     TextView mTvCancle;
    @Bind(R.id.title)
    TextView mTvTitle;
    Activity mActivity;
    public String tag;
    private ICallDialogCallBack listener;
    public DeleteDataDialog(Activity context,String tag) {
        super(context, R.style.Dialog);
        if (context instanceof HomeActivity){
            mActivity=(HomeActivity)context;
        }
        mContext = context;
        this.tag=tag;

    }

    public void setListener(ICallDialogCallBack listener) {
        this.listener = listener;
    }

    public interface ICallDialogCallBack {
         void onConfirmClick(DeleteDataDialog currentDialog);

         void onCancelClick(DeleteDataDialog currentDialog);

    }

    public interface DeleteDataInterface{
       public void delete();
    }
    public DeleteDataInterface deleteDataInterface;
    public void setInterface(DeleteDataInterface argDeleteDataInterface){
        deleteDataInterface=argDeleteDataInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.delete_data_dialog);
        ButterKnife.bind(this);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        WindowManager manager = mActivity.getWindowManager();
        Display display = manager.getDefaultDisplay();
//        if (GlobalCfg.IS_POTRAIT) {
//            params.width=(int) (display.getWidth()*0.83);
//            params.height=(int) (display.getHeight()*0.26);
//        } else {
//            params.width=(int) (display.getWidth()*0.47);
//            params.height=(int) (display.getHeight()*0.47);
//        }
        getWindow().setAttributes(params);
        mTvDownload.setOnClickListener(this);
        mTvCancle.setOnClickListener(this);
        setCanceledOnTouchOutside(false);
        if (tag!=null&&tag.equals("cach")){
            mTvDownload.setText(R.string.ok);
            mTvCancle.setText(R.string.cancel);
            mTvTitle.setText(R.string.str_clean_all_offline_cache);
        }else if (tag!=null&&tag.equals("HomeActivity")){
            mTvDownload.setText(R.string.ok);
            mTvCancle.setText(R.string.cancel);
            mTvTitle.setText(R.string.str_exit_app);
        }
        else if(tag != null && tag.equals("NaviFragment")){
            mTvDownload.setText(R.string.ok);
            mTvCancle.setText(R.string.cancel);
            mTvTitle.setText(R.string.str_exit_navi);
        }
        else if(tag != null && tag.equals("KeySearchFragment")){
            mTvDownload.setText(R.string.str_clean_all);
            mTvCancle.setText(R.string.cancel);
            mTvTitle.setText(R.string.str_clean_all_history);
        }else if(tag != null && tag.equals("VehicleDelete")){
            mTvDownload.setText(R.string.ok);
            mTvCancle.setText(R.string.cancel);
            mTvTitle.setText(R.string.str_clean_car_information);
        }
        else if(tag != null && tag.equals("VehicleWelfare")){
            mTvDownload.setText(R.string.i_know);
            mTvCancle.setText(R.string.str_check_coupon);
            mTvTitle.setText(R.string.str_get_car_wash_success);
        }
        else if(tag != null && tag.equals("continueNaving")){
            mTvDownload.setText("继续");
            mTvCancle.setText("取消");
            String address = CacheUtils.getInstance(mContext).getString(SettingCfg.NAVI_END_ADDRESS,"");
            String[] stStrs = address.split(",");
            mTvTitle.setText("是否继续上次导航？\n 目的地："+ stStrs[0]);
        }
        else if(tag != null && tag.equals("deleteOfflineMap")){
            mTvDownload.setText("确认");
            mTvCancle.setText("取消");
             mTvTitle.setText("确认删除离线数据？");
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_download:
                if (deleteDataInterface!=null){
                    deleteDataInterface.delete();
                }
                if(listener != null){
                    listener.onConfirmClick(this);
                }
                dismiss();
                break;
            case R.id.tv_cancle:
                if(listener != null){
                    listener.onCancelClick(this);
                }
                dismiss();
                break;
        }
    }
}
