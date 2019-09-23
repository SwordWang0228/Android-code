package com.lee.library.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.*;

/**
 * @author jv.lee
 */
public class WebViewEx extends WebView {

    private boolean isFailed = false;
    private boolean isPause = false;

    public WebViewEx(Context context) {
        super(context);
        init();
    }

    public WebViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WebViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        setWebContentsDebuggingEnabled(true);

        setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //不校验https证书
                handler.proceed();
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String scheme = request.getUrl().getScheme();
                if (!TextUtils.isEmpty(scheme) && !scheme.equals("http") && !scheme.equals("https")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().getApplicationContext().startActivity(intent);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }

            //开始加载网页
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                getSettings().setBlockNetworkImage(true);
                isFailed = true;
                if (webStatusListenerAdapter != null) {
                    webStatusListenerAdapter.callStart();
                    return;
                }
                if (webStatusCallBack != null) {
                    webStatusCallBack.callStart();
                }
            }

            //加载网页成功
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                getSettings().setBlockNetworkImage(false);
                if (!getSettings().getLoadsImagesAutomatically()) {
                    //设置wenView加载图片资源
                    getSettings().setBlockNetworkImage(false);
                    getSettings().setLoadsImagesAutomatically(true);
                }
                if (webStatusListenerAdapter != null && isFailed) {
                    webStatusListenerAdapter.callSuccess();
                    return;
                }
                if (webStatusCallBack != null && isFailed) {
                    webStatusCallBack.callSuccess();
                }
            }

            //加载网页错误
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                isFailed = false;
                if (webStatusListenerAdapter != null) {
                    webStatusListenerAdapter.callFailed();
                    return;
                }
                if (webStatusCallBack != null) {
                    webStatusCallBack.callFailed();
                }
            }
        });
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (webStatusListenerAdapter != null) {
                    webStatusListenerAdapter.callProgress(newProgress);
                    return;
                }
                if (webStatusCallBack != null) {
                    webStatusCallBack.callProgress(newProgress);
                }
            }
        });

    }

    long time;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (System.currentTimeMillis() - time >= 1000) {
            if (webStatusListenerAdapter != null) {
                time = System.currentTimeMillis();
                webStatusListenerAdapter.callScroll();
                return;
            }
            if (webStatusCallBack != null) {
                time = System.currentTimeMillis();
                webStatusCallBack.callScroll();
            }
        }
    }

    public void exResume() {
        if (isPause) {
            onResume();
        }
        isPause = false;
    }

    public void exPause() {
        onPause();
        isPause = true;
    }

    public void exDestroy() {
        setVisibility(GONE);
        clearCache(true);
        clearHistory();
        removeAllViews();
        destroy();
        isPause = false;
    }

    private WebStatusListenerAdapter webStatusListenerAdapter;

    public void addWebStatusListenerAdapter(WebStatusListenerAdapter webStatusListenerAdapter) {
        this.webStatusListenerAdapter = webStatusListenerAdapter;
    }

    public abstract static class WebStatusListenerAdapter implements WebStatusCallBack {
        @Override
        public void callStart() {

        }

        @Override
        public void callSuccess() {

        }

        @Override
        public void callFailed() {

        }

        @Override
        public void callProgress(int progress) {

        }

        @Override
        public void callScroll() {

        }
    }

    public interface WebStatusCallBack {
        void callStart();

        void callSuccess();

        void callFailed();

        void callProgress(int progress);

        void callScroll();
    }

    private WebStatusCallBack webStatusCallBack;

    public void setWebStatusCallBack(WebStatusCallBack webStatusCallBack) {
        this.webStatusCallBack = webStatusCallBack;
    }
}
