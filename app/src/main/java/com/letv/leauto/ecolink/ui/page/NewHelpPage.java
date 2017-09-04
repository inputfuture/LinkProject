package com.letv.leauto.ecolink.ui.page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.ui.base.BasePage;
import com.letv.leauto.ecolink.utils.Trace;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by why on 2017/2/14.
 */

public class NewHelpPage extends BasePage {
    int mIndex;
    @Bind(R.id.step1)
    TextView mStepTextView1;
    @Bind(R.id.step2)
    TextView mStepTextView2;
    @Bind(R.id.step_image_1)
    ImageView mStepImag1;
    @Bind(R.id.step_image_2)
    ImageView mStepImag2;
    private boolean mIsInit;

    public NewHelpPage(Context context, int index) {
        super(context);
        mIndex = index;
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.page_new_help, null);
        } else {
            view = inflater.inflate(R.layout.page_new_help_1, null);
        }
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        if (!mIsInit) {
            if (mIndex == 0) {
                mStepTextView1.setText(ct.getString(R.string.help_step1));
                mStepTextView2.setText(ct.getString(R.string.help_step2));
                mStepImag1.setImageResource(R.mipmap.help_step1);
                mStepImag2.setImageResource(R.mipmap.help_step2);


            } else if (mIndex == 1) {
                mStepTextView1.setText(ct.getString(R.string.help_step3));
                mStepTextView2.setText(ct.getString(R.string.help_step4));
                mStepImag1.setImageResource(R.mipmap.help_step3);
                mStepImag2.setImageResource(R.mipmap.help_step4);

            }
            mIsInit=true;
        }

    }
}
