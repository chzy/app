package com.chd.yunpan.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chd.yunpan.R;
import com.chd.service.SyncFileService;
import com.chd.yunpan.ui.adapter.MainFragmentPagerAdapter;
import com.chd.yunpan.ui.fragment.BaseFragment;
import com.chd.yunpan.ui.fragment.FileListFragment;
import com.chd.yunpan.ui.fragment.PhotoListFragment;
import com.chd.yunpan.utils.Constants;
import com.chd.yunpan.utils.ToastUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class MainFragmentActivity extends FragmentActivity implements OnClickListener {
	private View show;

	private ViewPager vp = null;

	private View selected = null;

	private RelativeLayout r = null;

	//private TextView netDisk = null;

	//private TextView photo = null;

	//private TextView other = null;

	private TextView title_txt = null;

	private int currentPage = 0;
	
	// 分享
	public static IWXAPI api;

	private final  int totolwins=2;


	private MainFragmentPagerAdapter adapter = null;
	private String[] headTitle = { "网盘0", "照片0", "其他"," "," "," " };
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);


		r = (RelativeLayout) View.inflate(this, R.layout.activity_main, null);
		setContentView(r);
		show = findViewById(R.id.show);
		initAllView(this);
		disShowHandler.sendEmptyMessageDelayed(1, 3000);
		Intent i  = new Intent(this, SyncFileService.class);
		startService(i);
		// 分享注册
				api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
				api.registerApp(Constants.APP_ID);



	}
	
	private boolean canBack = true;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

			Intent i=new Intent(this,MyspaceActivity.class);
			startActivity(i);

			return super.onKeyDown(keyCode, event);
		}




	public boolean onKeyDown0(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if(keyCode==KeyEvent.KEYCODE_BACK){
		  BaseFragment fragment = (BaseFragment) adapter.getItem(vp.getCurrentItem());
		  if(fragment.onBack()==false)
		  {
			  return true;
		  }
		  if(!canBack){
				canBack = true;
				new Thread(){
					public void run() {
						try {
							Thread.sleep(3000);
							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						canBack = false;
					}
				}.start();
				ToastUtils.toast(this, "再点击一次退出程序");
				return false;
			}

		}
		
	
		
	return super.onKeyDown(keyCode, event);
	}
	
	private Handler disShowHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Animation a = AnimationUtils.loadAnimation(MainFragmentActivity.this,
					R.anim.show_dis);
			a.setFillAfter(true);
			a.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					show.setVisibility(View.GONE);
				}
			});
			show.startAnimation(a);
		}
	};
	private int getWindowWith() {
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);

		return dm.widthPixels;

	}
	private void initAllView(Context context) {
		LayoutInflater inflater = getLayoutInflater();

		selected = inflater.inflate(R.layout.selected, r, false);
		//selected.setWidth( getWindowWith() / totolwins);
		//selected.setWidth( getWindowWith() );

//
//			//r.addView(selected);
		r.addView(selected, r.getChildCount() - 2);

		vp = (ViewPager) this.findViewById(R.id.MainActivityViewPager);



		//netDisk = (TextView) this.findViewById(R.id.MainActivityNetDisk);
		//netDisk.setOnClickListener(this);
		//title_txt=(TextView) this.findViewById(R.id.MainActivityTitle);

		//photo = (TextView) this.findViewById(R.id.MainActivityPhoto);
		//photo.setOnClickListener(this);

		//other = (TextView) this.findViewById(R.id.MainActivityOther);
		//other.setOnClickListener(this);

		initFragment();

		//Intent in = getIntent();
		////////////////////////
		Intent intent1 = getIntent();
		currentPage =intent1.getIntExtra("callpage", 0);
		currentPage=Math.min(5,currentPage);

		//title_txt=selected;
		title_txt=(TextView)selected.findViewById(R.id.global_head_title);
		title_txt.setText(R.string.mysapce_menu+currentPage);

		//vp.setCurrentItem(in.getIntExtra("currentItem",0));
		vp.setCurrentItem(currentPage);

	}

	/** 初始化viewPager的adapter */
	private void initFragment() {

		adapter = new MainFragmentPagerAdapter(getSupportFragmentManager());

		vp.setAdapter(adapter);
		//设置 vp  保留两个
//		vp.setOffscreenPageLimit(2);
//		vp.setOnPageChangeListener(new OnPageChangeListener() {
//
//			@Override
//			public void onPageSelected(int arg0) {
//				doAnimation(currentPage, arg0);
//				for (BaseFragment fragment : adapter.fragments) {
//					fragment.pageChange();
//				}
//			}
//
//			@Override
//			public void onPageScrolled(int arg0, float arg1, int arg2) {
//				// adapter.changeCurrentItem(arg0);
//
//			}
//
//			@Override
//			public void onPageScrollStateChanged(int arg0) {
//
//			}
//		});

	}

	// 标题的移动动画
	private void doAnimation(int fromPage, int toPage) {

		final int to = toPage;
		int offset = getWindowWith() / totolwins;
		Animation anim = new TranslateAnimation(fromPage * offset, toPage
				* offset, 0, 0);
		anim.setFillAfter(true);
		anim.setInterpolator(new DecelerateInterpolator());
		anim.setDuration(Math.abs(100*(fromPage-toPage)));
		anim.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				/*selected.setText(headTitle[to]);*/
				title_txt.setText(headTitle[to]);
				currentPage = to;
			}
		});
		selected.startAnimation(anim);

	}
	@Override
	public void onClick(View v) {
			switch (v.getId()) {
//			case R.id.MainActivityNetDisk:
//				vp.setCurrentItem(0);
//				break;

//			case R.id.MainActivityPhoto:
//				vp.setCurrentItem(1);
//				break;
//			case R.id.MainActivityOther:
//				vp.setCurrentItem(2);
//				break;
			default:
				int id=v.getId();

			}
	}
	public TextView getNetDisk() {
		return title_txt;
	}
	
	/** 分享到联系人 */
	public void sendSMS(String smsBody) {

		Uri smsToUri = Uri.parse("smsto:");

		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);

		intent.putExtra("sms_body", smsBody);

		startActivity(intent);

	}

	/** 分享到好友 */
	public void send(String msg1, String name) {
		// 初始化一个WXTextObject对象
		WXTextObject textObj = new WXTextObject();
		textObj.text = msg1;

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// 发送文本类型的消息时，title字段不起作用
		// msg.title = "Will be ignored";
		msg.description = name;
		// WXWebpageObject webpage = new WXWebpageObject();
		// webpage.webpageUrl = msg1;
		// WXMediaMessage msg = new WXMediaMessage(webpage);
		// msg.title = "" + name;
		// msg.description = "这是个下载连接   " + msg1;
		// Bitmap thumb = BitmapFactory.decodeResource(getResources(),
		// R.drawable.ic_launcher);
		// msg.thumbData = Util.bmpToByteArray(thumb, true);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	/** 分享到朋友圈 */
	public void sendFriends(String msg1, String name) {
		// WXWebpageObject webpage = new WXWebpageObject();
		// webpage.webpageUrl = msg1;
		// WXMediaMessage msg = new WXMediaMessage(webpage);
		// msg.title = "";
		// msg.description = "这是一个下载连接   " + msg1;
		// Bitmap thumb = BitmapFactory.decodeResource(getResources(),
		// R.drawable.ic_launcher);
		// msg.thumbData = Util.bmpToByteArray(thumb, true);
		WXTextObject textObj = new WXTextObject();
		textObj.text = msg1;

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// 发送文本类型的消息时，title字段不起作用
		// msg.title = "Will be ignored";
		msg.description = name;
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);
	}
	
	

	/** 分享邮箱 */
	public void sendEmail(String msg1, String name) {
		// 建立Intent 对象
		final Intent emailIntent = new Intent(
				android.content.Intent.ACTION_SEND);
		// 设置文本格式
		emailIntent.setType("plain/text");
		// 设置对方邮件地址
		// emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new
		// String[]{[email={[email=email=%7B%C3%97%C3%97%C3%97%C3%97%C3%97%C3%97@gmail.com]××××××@gmail.com[/email]]××××××@gmail.com[/email]});
		// 设置标题内容
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, name);
		// 设置邮件文本内容
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg1);
		// 启动一个新的ACTIVITY,"Sending mail..."是在启动这个ACTIVITY的等待时间时所显示的文字
		startActivity(Intent.createChooser(emailIntent, "FYX Sending mail..."));
	}

	/** 分享到其他 */
	public void sendOther(String msg) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "fenxiang");
		intent.putExtra(Intent.EXTRA_TEXT, msg);
		startActivity(Intent.createChooser(intent, getTitle()));
	}

	/**
	 * @param "content要分享的字符串
	 **/
	public void setClipBoard(String content) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			android.content.ClipData clip = android.content.ClipData
					.newPlainText("label", content);
			clipboard.setPrimaryClip(clip);
		} else {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

			clipboard.setText(content);
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(PhotoShowActivity.delete){
			((FileListFragment)adapter.fragments[0]).refresh();
			((PhotoListFragment)adapter.fragments[1]).refreshWithDataChange();
			PhotoShowActivity.delete =false;
		}
	}
	


}
