package com.facebook.se.apac.example.liyuhk.didiads;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class FBEWebViewActivity extends AppCompatActivity {

    private WebView fbeWebView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbe_webview);

        fbeWebView = (WebView) findViewById(R.id.fbe_webview);
        fbeWebView.loadUrl("https://m.facebook.com/ads/dia");
    }
}
