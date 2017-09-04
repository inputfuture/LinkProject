package com.letv.leauto.ecolink.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.ui.dialog.NetworkConfirmDialog;
import com.letv.leauto.ecolink.utils.Trace;
import com.letvcloud.cmf.utils.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhaotongkai on 2016/9/29.
 */
public class DisclaimerFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = DisclaimerFragment.class.getSimpleName();

    @Bind(R.id.iv_back)
    ImageView mBack;
    @Bind(R.id.webview)
    WebView mWebView;
    @Bind(R.id.tv_title)
    TextView mTitle;

    private String mUrl;

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if (GlobalCfg.IS_POTRAIT) {
            view = inflater.inflate(R.layout.disclaimer_layout_p, null);
        } else {
            view = inflater.inflate(R.layout.disclaimer_layout, null);
        }

        ButterKnife.bind(this, view);
        mTitle.setText(R.string.notification);
        mBack.setOnClickListener(this);
        return view;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        initWebView();
    }

    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        mUrl = "file:///android_asset/instruction.html";

        mWebView.loadUrl(mUrl);
        mWebView.setBackgroundColor(0);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webview, String url) {
                if (url != null && url.startsWith("tel:")) {
                    final String number = url.substring(4);
                    NetworkConfirmDialog dialog = new NetworkConfirmDialog(getActivity(), number, R.string.make_call, R.string.cancel);
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
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);
        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*getFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingAboutFragment()).commitAllowingStateLoss();*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                getFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingAboutFragment()).commitAllowingStateLoss();
                break;
        }
    }
}
