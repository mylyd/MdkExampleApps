package com.example.mdkfastprintingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.example.mdkfastprintingapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Web创建激活码跳转界面
 */
public class RegistrationActivatioCodeActivity extends AppCompatActivity {

    @BindView(R.id.web_view)
    WebView webView;
    String url = "http://www.jkzcloud.com/";
    @BindView(R.id.btn_back)
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_activatio_code);
        ButterKnife.bind(this);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    @OnClick(R.id.btn_back)
    public void onClick() {
        startActivity(new Intent(this,ActivateLoginActivity.class));
        finish();
    }
}
