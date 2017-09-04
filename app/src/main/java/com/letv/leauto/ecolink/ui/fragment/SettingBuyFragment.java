package com.letv.leauto.ecolink.ui.fragment;

import com.tencent.smtt.export.external.interfaces.SslError;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import android.widget.ImageView;

import com.letv.leauto.ecolink.R;
import com.letv.leauto.ecolink.cfg.GlobalCfg;
import com.letv.leauto.ecolink.ui.HomeActivity;
import com.letv.leauto.ecolink.ui.base.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by guo on 2017/7/7.
 */

public class SettingBuyFragment extends BaseFragment implements View.OnClickListener{

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.wv_bug)
    WebView mWebView;

    @Override
    protected View initView(LayoutInflater inflater) {
        View view;
        if(GlobalCfg.IS_POTRAIT){
            view = inflater.inflate(R.layout.fragment_thincar_buy, null);
        }else {
            view = inflater.inflate(R.layout.fragment_thincar_buy_l, null);
        }
        ButterKnife.bind(this, view);
        ivBack.setOnClickListener(this);
        return view;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    public void onResume() {
        super.onResume();
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(webSettings.LOAD_DEFAULT);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        mWebView.loadUrl("http://www.lemall.com/product/products-pid-1001138.html");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                destoryWebview();
                ((HomeActivity) mContext).getSupportFragmentManager().popBackStack();
                break;
        }
    }

    private void destoryWebview(){
        if(mWebView!=null){
            mWebView.loadDataWithBaseURL(null,"", "", "utf-8", null);
            mWebView.clearHistory();
            ((ViewGroup)mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        destoryWebview();
        super.onDestroy();
    }
}
