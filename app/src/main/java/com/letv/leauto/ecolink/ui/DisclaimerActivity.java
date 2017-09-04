package com.letv.leauto.ecolink.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.view.View;

import com.leauto.link.lightcar.ScreenRecordActivity;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.ui.base.BaseActivity;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;
import com.letv.leauto.ecolink.utils.CacheUtils;
import com.letv.leauto.ecolink.utils.Trace;
import com.letvcloud.cmf.utils.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhaotongkai on 2016/9/30.
 */
public class DisclaimerActivity extends BaseActivity{

    private static final String TAG = DisclaimerActivity.class.getSimpleName();

    public static final String NEVER_MIND = "never_mind";

    public static boolean firstBoot = true;

    @Bind(R.id.btn_access)
    TextView mAccess;
    @Bind(R.id.iv_back)
    ImageView mBack;
    @Bind(R.id.tv_line)
    TextView mSpliter;

    @Bind(R.id.chk_forever)
    CheckBox mNevermind;
    @Bind(R.id.tv_title)
    TextView mTitle;
    @Bind(R.id.rl_layout)
    RelativeLayout rl_layout;

    @Bind(R.id.webview)
    WebView mWebView;



    @Override
    protected void initView() {
        if (GlobalCfg.IS_POTRAIT) {
            setContentView(R.layout.disclaimer_activity_p);
        } else {
            setContentView(R.layout.disclaimer_activity);
        }
        ButterKnife.bind(this);
        mTitle.setText(R.string.notification);

        mAccess.setOnClickListener(this);
        mBack.setVisibility(View.GONE);
        mSpliter.setVisibility(View.GONE);
        firstBoot = false;
        mAccess.setVisibility(View.INVISIBLE);
        mNevermind.setVisibility(View.INVISIBLE);
        initWebView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_access:

                CacheUtils.getInstance(mContext).putBoolean(NEVER_MIND, mNevermind.isChecked());
                Intent intent=new Intent(mContext,HomeActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        String mUrl = "file:///android_asset/instruction.html";
        mWebView.setBackgroundColor(0);

        mWebView.loadUrl(mUrl);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webview, String url) {
                if (url != null && url.startsWith("tel:")) {
                    final String number = url.substring(4);
                    NetworkConfirmDialog dialog = new NetworkConfirmDialog(DisclaimerActivity.this, number, R.string.make_call, R.string.cancel);
                    dialog.setListener(new NetworkConfirmDialog.OnClickListener() {
                        @Override
                        public void onConfirm(boolean checked) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_CALL);
                            //需要拨打的号码
                            intent.setData(Uri.parse("tel:" + number));
                            startActivity(intent);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                    dialog.show();
                    return true;
                }
                webview.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Logger.e(TAG, "onPageStarted  url:" + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebView.setVisibility(View.VISIBLE);
                mAccess.setVisibility(View.VISIBLE);
                mNevermind.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                Trace.Debug(TAG, "--->url:" + url);

            }
        });
    }

    private void startRecordActivity(boolean isThincar) {
        Intent intent = new Intent(mContext, ScreenRecordActivity.class);
        if (isThincar) {
            intent.setAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        } else {
            intent.setAction("com.letv.leauto.ecolink.adb.launch");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
