package com.chd.yunpan.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
 * @FileName: com.chd.yunpan.utils.NetUtils
 * @author: liumj
 * @date:2016-05-24 14:29
 * OS:Mac 10.10
 * Developer Kits:AndroidStudio 1.3
 */

public class NetUtils {

	public static boolean isGPRS(Context ctx){
		ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo.State state = connManager.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).getState(); // 获取网络连接状态
		if (NetworkInfo.State.CONNECTED == state) { // 判断是否正在使用GPRS网络
			return true;
		}else{
			return false;
		}
	}

}
