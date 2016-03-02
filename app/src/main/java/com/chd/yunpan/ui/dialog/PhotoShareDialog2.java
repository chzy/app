package com.chd.yunpan.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.chd.TClient;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.net.NetworkUtils;
import com.chd.yunpan.ui.PhotoShowActivity;

/*import com.chd.yunpan.parse.entity.FileDataEntity;*/

public class PhotoShareDialog2 extends Dialog implements OnClickListener {



	private View back = null;

	private View weixin = null;
	private View friends = null;
	private View sms = null;
	private View email = null;
	private View line = null;
	private View other = null;

	private Button cancel = null;

	private Context context = null;

	private FileInfo0 entity;
	private ListView lv;

	public PhotoShareDialog2(Activity activity, /*FileDataFatherEntity*/FileInfo0 entity) {
		super(activity);
		this.entity = entity;
		this.context = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.jd_menu_share);
		setView();
	}

	public void setView() {
		weixin = findViewById(R.id.menu_share_weixin);
		friends = findViewById(R.id.menu_share_friends);
		sms = findViewById(R.id.menu_share_sms);
		email = findViewById(R.id.menu_share_e_mail);
		line = findViewById(R.id.menu_share_line);
		other = findViewById(R.id.menu_share_other);
		back = findViewById(R.id.menu_share_back);
		weixin.setOnClickListener(this);
		friends.setOnClickListener(this);
		sms.setOnClickListener(this);
		email.setOnClickListener(this);
		line.setOnClickListener(this);
		other.setOnClickListener(this);
		back.setOnClickListener(this);
	}
	

	private int getWindowHeight() {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		return dm.widthPixels;
	}

	

	Dialog dialog = null;

	public void showDialog() {

		Window window = getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.mystyle); // 添加动画
		show();
		WindowManager windowManager = ((PhotoShowActivity)context).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = display.getWidth();
		getWindow().setAttributes(params);
		// lp.x = 0;
		// lp.y = getWindowHeight();
		window
				.setBackgroundDrawableResource(R.drawable.background_dialog);
	}


	public void disDialog() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
		case R.id.menu_share_back:
			dismiss();
			break;
		case R.id.menu_share_weixin:
			
		case R.id.menu_share_friends:
		case R.id.menu_share_sms:
		case R.id.menu_share_e_mail:
		case R.id.menu_share_line:
		case R.id.menu_share_other:
			if(NetworkUtils.isNetworkAvailable(context)){
			new Thread(){
				public void run() {
					//FileDownLoadLinkedEnitity enitity=  FileDownLoadLinkedUtil.getFileLinked(context,entity.getFid());
					String url = null;
					try {
						url = TClient.getinstance().CreateShare(entity.getFtype(),entity.getObjid());
					} catch (Exception e) {
						//e.printStackTrace();
					}
					Message message =handler3.obtainMessage();

					if(url!=null){
						message.what = v.getId();
						message.obj = url;
						//share(v.getId(), enitity.getUrl().getfPath());
					}else {
						//share(0, enitity.getUrl().getfPath());
						message.what = 0;
					}
				message.sendToTarget();
				}
			}.start();
			showDialog();
			}else {
				Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		dismiss();

	}
	
	
	Handler handler3 = new Handler(){
		public void handleMessage(android.os.Message msg) {
			disDialog();
			share(msg.what, msg.obj.toString());
			
		}
	};
	
	public void share(int id,String msg){
		switch (id) {
		case 0:
			Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
			break;
		case R.id.menu_share_weixin:
			((PhotoShowActivity)context).send(msg,entity.getFilename());
			break;
		case R.id.menu_share_friends:
			((PhotoShowActivity)context).sendFriends(msg,entity.getFilename());
			break;
		case R.id.menu_share_sms:
			((PhotoShowActivity)context).sendSMS(msg);
			break;
		case R.id.menu_share_e_mail:
			((PhotoShowActivity)context).sendEmail(msg, entity.getFilename());
			break;
		case R.id.menu_share_line:
			((PhotoShowActivity)context).setClipBoard(msg);
			Toast.makeText(context, "已经复制，可粘贴到其他文本框发送给好友", Toast.LENGTH_SHORT).show();
			break;
		case R.id.menu_share_other:
			((PhotoShowActivity)context).sendOther(msg);
			break;
			
			
		}
	}
	

}
