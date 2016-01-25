package com.chd.photo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chd.base.Ui.ActiveProcess;
import com.chd.yunpan.R;

/**
 * @description
 * @FileName: com.chd.photo.ui.DownListActivity
 * @author: liumj
 * @date:2016-01-20 20:17
 * OS:Mac 10.10
 * Developer Kits:AndroidStudio 1.5
 */

public class DownListActivity extends ActiveProcess implements View.OnClickListener{


    private ListView listView;
    private TextView tv_center;
    private ImageView iv_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downlist);
        listView = (ListView) findViewById(R.id.DOWNLOADLIST_LV);
        tv_center = (TextView) findViewById(R.id.tv_center);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        tv_center.setText("任务列表");
        iv_left.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_left:
                onBackPressed();
                break;
        }
    }
}
