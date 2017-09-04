package com.letv.leauto.ecolink.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import android.widget.ImageView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.Constant;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;
import com.letv.leauto.ecolink.utils.Trace;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shimeng on 14/4/26.
 */
public class SettingCustomSerFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.cusWebView)
    WebView mWebView;
    @Bind(R.id.iv_back)
    ImageView iv_back;


    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        view = inflater.inflate(R.layout.activity_setting_cusser, null);
        ButterKnife.bind(this, view);
        iv_back.setOnClickListener(this);
        loadingWeb();
        return view;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @SuppressLint("ResourceAsColor")
    private void loadingWeb() {
        mWebView.setDrawingCacheBackgroundColor(getResources().getColor(android.R.color.white));
        mWebView.setFocusableInTouchMode(true);
        mWebView.setFocusable(true);
        mWebView.setAnimationCacheEnabled(false);
        mWebView.setDrawingCacheEnabled(true);
        mWebView.setBackgroundColor(mContext.getResources().getColor(
                android.R.color.white));

        mWebView.setWillNotCacheDrawing(false);
        mWebView.setAlwaysDrawnWithCacheEnabled(true);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setSaveEnabled(true);

        initializeSettings(mWebView.getSettings(), mContext);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
//				view.loadUrl(failingUrl);
                Trace.Error("WebView", "the url is not ok ..;errorCode:" + errorCode + "..loading url:" + failingUrl);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              String url) {
                WebResourceResponse response = super.shouldInterceptRequest(view, url);

                Trace.Debug("WebView", "shouldInterceptRequest 's web url:" + url);
                return response;
            }


        });
        //mWebView.loadUrl("https://eco-api.meiqia.com/dist/standalone.html?eId=1656");
        mWebView.loadUrl("http://bbs.le.com/forum.php?mod=forumdisplay&fid=1446&page=1");
        mWebView.setWebChromeClient(new MyWebViewClient(new WebChromeClient()) {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }

            @Override
            public void onReceivedTouchIconUrl(WebView view, String url,
                                               boolean precomposed) {
                super.onReceivedTouchIconUrl(view, url, precomposed);
            }

            @Override
            public void onRequestFocus(WebView view) {
                super.onRequestFocus(view);
            }


        });

		/* Liyan 20141014 页面调用 */
        // mWebView.addJavascriptInterface(new JSApi(), "AppJs");
    }

    @SuppressLint("NewApi")
    public void initializeSettings(WebSettings settings, Context context) {
        settings.setJavaScriptEnabled(true);
        settings.setDefaultFontSize(13);

        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        settings.setTextZoom(100);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCachePath(context.getCacheDir().toString());
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setGeolocationDatabasePath(context.getCacheDir()
                .getAbsolutePath());
        settings.setDatabaseEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setAllowContentAccess(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setUseWideViewPort(true);

        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);

    }

    private class MyWebViewClient extends WebChromeClient {
        private WebChromeClient mClient;

        public MyWebViewClient(WebChromeClient client) {
            mClient = client;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
        }


    }

    @Override
    protected void notificationEvent(int keyCode) {
        Trace.Debug("notificationEvent->keyCode:" + keyCode);

        switch (keyCode) {
            case Constant.KEYCODE_DPAD_BACK:
                /*((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingAboutFragment(), "SettingAboutFragment").commitAllowingStateLoss();*/
                break;
            default:
                break;
        }
        super.notificationEvent(keyCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                if(!((HomeActivity) mContext).isPopupWindowShow) {
                    ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.setting_frame, new SettingAboutFragment(), "SettingAboutFragment").commitAllowingStateLoss();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SettingCustomSerFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingCustomSerFragment");
    }

}
