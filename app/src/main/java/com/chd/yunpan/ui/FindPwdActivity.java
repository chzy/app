package com.chd.yunpan.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.yunpan.R;

public class FindPwdActivity extends Activity implements View.OnClickListener {

	private WebView web = null;

	private WebSettings ws = null;

	private ImageView iv_left;
	private TextView tv_title;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		this.setContentView(R.layout.lz_find_password);

		iv_left= (ImageView) findViewById(R.id.iv_left);
		tv_title= (TextView) findViewById(R.id.tv_title);

		tv_title.setText("找回密码");
		iv_left.setOnClickListener(this);

		web = (WebView) this.findViewById(R.id.findPwdWebView);
		ws = web.getSettings();
		ws.setJavaScriptEnabled(true);
		ws.setDefaultTextEncodingName("utf-8");
		web.setWebChromeClient(new WebChromeClient());
		web.setWebViewClient(new WebViewClient());
		web.loadUrl("http://fyimail.vicp.net:1080/pwdapp.html");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (web.canGoBack()) {
				web.goBack();
			} else {
				this.finish();
			}
		}

		return false;
	}

	@Override
	public void onClick(View view) {
		int id=view.getId();
		switch (id){
			case R.id.iv_left:
				onBackPressed();
				break;
		}


	}
}
