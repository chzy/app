package com.chd.yunpan.utils;

import android.os.CountDownTimer;
import android.widget.Button;

/**
 * @description
 * @FileName: com.chd.yunpan.utils.TimeCount
 * @author: liumj
 * @date:2016-02-03 09:53
 * OS:Mac 10.10
 * Developer Kits:AndroidStudio 1.5
 */

public class TimeCount extends CountDownTimer{
    private Button btn;
    public TimeCount(long millisInFuture, long countDownInterval,Button v) {
        super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        this.btn=v;
    }
    @Override
    public void onFinish() {//计时完毕时触发
        btn.setText("重新验证");
        btn.setClickable(true);
    }
    @Override
    public void onTick(long millisUntilFinished){//计时过程显示
        btn.setClickable(false);
        btn.setText(millisUntilFinished /1000+"秒");
    }
}
