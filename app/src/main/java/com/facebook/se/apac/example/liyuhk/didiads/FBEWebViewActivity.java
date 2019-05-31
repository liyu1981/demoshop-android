package com.facebook.se.apac.example.liyuhk.didiads;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FBEWebViewActivity extends AppCompatActivity {

    private WebView fbeWebView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbe_webview);

        fbeWebView = (WebView) findViewById(R.id.fbe_webview);
        try {
            String webhook_uri = URLEncoder.encode("http://www.didi-ads.com/fbe_mobile_native_hack/callback.php", "utf-8");
            fbeWebView.loadUrl("https://m.liyuhk.sb.facebook.com/ads/dia?m=mobilenative&webhook=" + webhook_uri);
        } catch(UnsupportedEncodingException e) {

        }
    }
}
