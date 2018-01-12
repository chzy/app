package com.chd.base;

import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chd.base.Ui.ActiveProcess;

/**
 * @version _ooOoo_
 *          o8888888o
 *          88" . "88
 *          (| -_- |)
 *          O\  =  /O
 *          ____/`---'\____
 *          .'  \\|     |//  `.
 *          /  \\|||  :  |||//  \
 *          /  _||||| -:- |||||-  \
 *          |   | \\\  -  /// |   |
 *          | \_|  ''\---/''  |   |
 *          \  .-\__  `-`  ___/-. /
 *          ___`. .'  /--.--\  `. . __
 *          ."" '<  `.___\_<|>_/___.'  >'"".
 *          | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *          \  \ `-.   \_ __\ /__ _/   .-` /  /
 *          ======`-.____`-.___\_____/___.-`____.-'======
 *          `=---='
 *          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *          佛祖保佑       永无BUG
 * @description
 * @FileName: com.chd.base.UILActivity
 * @author: liumj
 * @date:2016-03-04 09:13
 * OS:Mac 10.10
 * Developer Kits:AndroidStudio 1.3
 */

public class UILActivity extends ActiveProcess {

    protected MaterialDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title("正在加载");
        builder.cancelable(false);
        builder.canceledOnTouchOutside(false);
        waitDialog=builder.build();
    }


    public void showWaitDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (waitDialog != null)
                    waitDialog.show();
            }
        });
    }


    public void dismissWaitDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (waitDialog != null)
                    waitDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (waitDialog != null) {
            waitDialog.dismiss();
            waitDialog = null;
        }
    }
}
