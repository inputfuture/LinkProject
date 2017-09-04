package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.database.model.ParkingDetail;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.ui.fragment.EasyStopFragment;
import com.letv.leauto.ecolink.utils.DensityUtils;
import com.letv.leauto.ecolink.utils.ShadowProperty;
import com.letv.leauto.ecolink.utils.ShadowViewHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhuyanbo on 16/09/05.
 */
public class EasyStopPage extends BasePage implements View.OnClickListener {

    @Bind(R.id.easy_stop_navi_button)
    RelativeLayout easy_stop_navi_button;

    @Bind(R.id.easy_stop_text_layout)
    LinearLayout easy_stop_text_layout;

    @Bind(R.id.easy_stop_navi_layout)
    RelativeLayout easy_stop_navi_layout;

    @Bind(R.id.easy_stop_address)
    TextView easy_stop_address;

    @Bind(R.id.easy_stop_count)
    TextView easy_stop_count;

    @Bind(R.id.easy_stop_distance)
    TextView easy_stop_distance;

    @Bind(R.id.easy_stop_first_hour)
    TextView easy_stop_first_hour;

    @Bind(R.id.easy_stop_park_name)
    TextView easy_stop_park_name;

    private EasyStopFragment mEasyStopFragment;

//    public Handler mHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            switch (msg.what) {
//                case MSG_DELAY_CLICK_WE:
//                    menu_local.setEnabled(true);
//                    break;
//                case MSG_DELAY_CLICK_LIVE:
//                    menu_live.setEnabled(true);
//                    break;
//                default:
//                    break;
//            }
//        }
//    };

    public EasyStopPage(Context context, EasyStopFragment fragment) {
        super(context);
        this.mEasyStopFragment = fragment;
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view = initOrietantion(inflater);
        ButterKnife.bind(this, view);
        ShadowViewHelper.bindShadowHelper(new ShadowProperty().setShadowColor(0x77000000).setShadowDx(DensityUtils.dp2px(ct, 0.5f)
        ).setShadowRadius(DensityUtils.dp2px(ct, 3)), easy_stop_text_layout, ct.getResources().getColor(R.color.showposition_bgcolor));

        easy_stop_navi_button.setOnClickListener(this);
        return view;

    }

    @Override
    public void initData() {
    }


    /**
     * 横竖屏初始化
     *
     * @param inflater
     * @return
     */
    private View initOrietantion(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.fragment_easy_stop_page_item, null);
        } else {
            view = inflater.inflate(R.layout.fragment_easy_stop_page_item_l, null);
        }

        return view;
    }


    public void initData(ParkingDetail detail, double distance) {
        easy_stop_address.setText(detail.address);
        easy_stop_count.setText(ct.getString(R.string.str_parking_lots_num)+detail.count);
        easy_stop_distance.setText(distance+"KM");
        easy_stop_first_hour.setText(ct.getString(R.string.str_price)+detail.firstHour+ct.getString(R.string.str_price_param));
        easy_stop_park_name.setText(detail.parkName);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.easy_stop_navi_button:
                mEasyStopFragment.startToNavi();
                break;
            default:
                break;
        }
    }


    @Override
    public void onResume() {


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
