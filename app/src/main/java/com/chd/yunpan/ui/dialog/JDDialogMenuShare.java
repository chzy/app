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

import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.net.NetworkUtils;
import com.chd.yunpan.ui.MainFragmentActivity;
import com.chd.yunpan.utils.FileDownLoadLinkedUtil;

import java.util.ArrayList;
import java.util.List;

public class JDDialogMenuShare extends Dialog implements OnClickListener {



	private View back = null;

	private View weixin = null;
	private View friends = null;
	private View sms = null;
	private View email = null;
	private View line = null;
	private View other = null;

	private Button cancel = null;

	private Context context = null;


	private ListView lv;
	
	List<Integer> integers = new ArrayList<Integer>();
	
	private FileInfo0 dataEntity;


	public JDDialogMenuShare(Context context,FileInfo0 dataEntity) {
		super(context);
		this.context = context;
		this.dataEntity = dataEntity;
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
		email =findViewById(R.id.menu_share_e_mail);
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

	private void dialogAnimation(final Dialog d, View v, int from, int to,
			boolean needDismiss) {
		if(needDismiss){
			dismiss();
		}else{
			showDialog();
		}
		
	}

	public void showDialog() {

		Window window = getWindow();
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.mystyle); // 添加动画
		show();
		WindowManager windowManager = ((Activity)context).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = display.getWidth();
		getWindow().setAttributes(params);
		// lp.x = 0;
		// lp.y = getWindowHeight();
		window
				.setBackgroundDrawableResource(R.drawable.background_dialog);
	}
	private OnConfirmListener onConfirmListener = null;

	public interface OnConfirmListener {
		void confirm();
	}

	public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
		this.onConfirmListener = onConfirmListener;
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
						
						/*FileDownLoadLinkedEnitity enitity=  FileDownLoadLinkedUtil.getFileLinked(context, dataEntity.getObjid());*/
						String url=FileDownLoadLinkedUtil.getFileLinked(context, dataEntity.getFtype(),dataEntity.getObjid());
						
						Message message =handler.obtainMessage();

						if(/*enitity.isState()*/url!=null && url.length()>10){
							message.what = v.getId();
							message.obj = /*enitity.getUrl().toString()*/url;
							//share(v.getId(), enitity.getUrl().getfPath());
						}else {
							//share(0, enitity.getUrl().getfPath());
							message.what = 0;
						}
						message.sendToTarget();
					}
				}.start();
			}else {
				Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();
			}
			
			break;
		}
		dismiss();

	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			share(msg.what, msg.obj.toString());
		}
	};
	
	public void share(int id,String msg){
		switch (id) {
		case 0:
			Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
			break;
		case R.id.menu_share_weixin:
			((MainFragmentActivity)context).send(msg,dataEntity./*getN()*/getObjid());
			break;
		case R.id.menu_share_friends:
			((MainFragmentActivity)context).sendFriends(msg,dataEntity./*getN()*/getObjid());
			break;
		case R.id.menu_share_sms:
			((MainFragmentActivity)context).sendSMS(msg);
			break;
		case R.id.menu_share_e_mail:
			((MainFragmentActivity)context).sendEmail(msg,dataEntity./*getN()*/getObjid());
			break;
		case R.id.menu_share_line:
			((MainFragmentActivity)context).setClipBoard(msg);
			Toast.makeText(context, "已经复制，可粘贴到其他文本框发送给好友", Toast.LENGTH_SHORT).show();
			break;
		case R.id.menu_share_other:
			((MainFragmentActivity)context).sendOther(msg);
			break;
			
			
		}
	}

}
