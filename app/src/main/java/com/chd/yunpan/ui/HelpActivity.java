package com.chd.yunpan.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.yunpan.R;

/**
 * @description
 * @FileName: com.chd.yunpan.ui.HelpActivity
 * @author: liumj
 * @date:2016-02-01 17:50
 * OS:Mac 10.10
 * Developer Kits:AndroidStudio 1.5
 */

public class HelpActivity extends Activity implements View.OnClickListener {

    TextView tv_center;
    private ImageView iv_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        tv_center = (TextView) findViewById(R.id.tv_center);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        boolean isHelp = getIntent().getBooleanExtra("isHelp", false);
        if(isHelp){
            tv_center.setText("帮助");
        }else{

            tv_center.setText("关于沃空间");
        }
        iv_left.setOnClickListener(this);
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
