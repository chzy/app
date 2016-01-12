package com.chd.yunpan.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;

import com.chd.Entity.FilesListEntity;
import com.chd.TClient;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.net.ExecRunable;
import com.chd.yunpan.ui.adapter.PhotoViewPager_Adapter;
import com.chd.yunpan.ui.fragment.popupwindow.PhotoPopMenuShow;
import com.chd.yunpan.utils.TimeUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

/*import com.chd.yunpan.parse.FileListParse;
import com.chd.yunpan.parse.entity.FileDataEntity;
import com.chd.yunpan.parse.entity.FilesListEntity;*/

public class PhotoShowActivity extends Activity {
	private PhotoPopMenuShow menuShow = null;
	private ViewPager page = null;
	private int currpag = 0;
	
	private FileInfo0 dataEntity;
	
	private FilesListEntity<FileInfo0> entity;
	
	// 分享
	public static IWXAPI api;

	private ProgressDialog pd;


	public boolean isDelete() {
		return delete;
	}
	public void setDelete(boolean delete) {
		PhotoShowActivity.delete = delete;
	}
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		delete =false;
		setContentView(R.layout.photo_show);
		Intent intent = getIntent();
		currpag = intent.getIntExtra("position", -1);
		dataEntity  = (FileInfo0) intent.getSerializableExtra("entity");
		
		page = (ViewPager) findViewById(R.id.photo_show_viewpager);
	
		api = MainFragmentActivity.api;
		
		page.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				currpag = arg0;
				FileInfo0 f=entity.getList().get(currpag);
				menuShow.setTitle(TimeUtils.getTime(f.getLastModified()  *1000),
						(currpag + 1) + "/" + entity.getCount());
				for (int i = 0; i < entity.getCount(); i++) {
					entity.getList().get(i).setIsChecked(false);	/*.isChecked = false*/
				} 
				entity.getList()/*getFatherEntities()*/.get(arg0).setIsChecked(true);   /*.isChecked=true*/
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		
		pd = new ProgressDialog(this);
		pd.setTitle("正在加载");
		pd.show();
		
		//
		downFromWeb(0);
	}
	/**
	 * 时间排序
	 */
	public static final String USER_PTIME = "user_ptime";
	/**
	 * 大小排序
	 */
	public static final String FILE_SIZE="file_size";
	/**
	 * 文件名
	 */
	public static final String FILE_NAME = "file_name";
	
	/**
	 * 降序
	 */
	public static final int DESC = 1;
	
	/**
	 * 升序
	 */
	public static final int ASC = 0;
	
	public void downFromWeb(final int offset){
		ExecRunable.execRun(new Thread(){
			public void run() {
			/*ShareUtils utils = new ShareUtils(PhotoShowActivity.this);
			String url = utils.getURL()+"/a1/index?ct=list&aid=1&cid="+dataEntity.getPid()+"&o="+USER_PTIME+"&asc="+ASC+"&offset="+offset+"&limit=100&type="+2;
			*/
			try {
				
/*
				String result = HttpUtils.GetStringForHttpGet(utils.getCookieUtil(), null, url, 1);
				FileListParse parse = new FileListParse();
*/
				/* FilesListEntity entity = parse.parse(result);*/
				FilesListEntity entity= TClient.getinstance().queryFileList(FTYPE.PICTURE,offset,1000);
				Message msg = new Message();
				if(offset==0){
				msg.what = 1;
				}else {
					msg.what = 2;
				}
				msg.obj = entity;
				handler.sendMessage(msg);
				
			} catch (Exception e) {
				e.printStackTrace();
				
				handler.sendEmptyMessage(-1);
			}
			
			}
		});
	}
//	public RequestParam getFileList000(int type,int offset,int cid,String order,int ascOrdesc){
//		RequestParam param = new RequestParam();
//		ShareUtils utils = new ShareUtils(this);
//		if(type==-1){
//			param.url = utils.getURL()+"/a1/index?ct=list&aid=1&cid="+cid+"&o="+order+"&asc="+ascOrdesc+"&offset="+offset+"&limit=100";
//		}else {
//			param.url = utils.getURL()+"/a1/index?ct=list&aid=1&cid="+cid+"&o="+order+"&asc="+ascOrdesc+"&offset="+offset+"&limit=100&type="+type;
//		}
//		param.method = RequestParam.GET;
//		param.parse = new FileListParse();
//		return param;
//	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(PhotoShowActivity.this==null){
				return;
			}
			pd.dismiss();
			switch (msg.what) {
			case -1://下载失败
				
				break;
			case 1:
				//下载成功
				
				entity = (FilesListEntity) msg.obj;
				getCurrentPosition();
				if(currpag!=-1){
					setAdapter();
				}
				
				if(entity.getCount()>entity.getCount()){
					downFromWeb(entity.getCount());
				}
				
				break;
			case 2:
				if(adapter==null){
					if(getCurrentPosition()!=-1){
						setAdapter();
					}
				}
				entity.merge((FilesListEntity) msg.obj);
				if(entity.getCount()>entity.getCount()){
					downFromWeb(entity.getCount());
				}
				if(adapter!=null)
				adapter.notifyDataSetChanged();
			default:
				break;
			}
		}
	};
	
	private PhotoViewPager_Adapter adapter ;
	
	
	
	public FilesListEntity getEntity() {
		return entity;
	}
	
	public void setAdapter(){
		menuShow = new PhotoPopMenuShow(this);
		adapter = new PhotoViewPager_Adapter(this, menuShow);
		page.setAdapter(adapter);
		
		getCurrentPosition();
		page.setCurrentItem(currpag);
		FileInfo0 f= entity.getList().get(currpag);
		menuShow.setTitle(TimeUtils.getTime(f.getLastModified()),
				(currpag + 1) + "/" + entity.getCount());
	}
	
	public int getCurrentPosition(){
		for (int i = 0; i < entity.getCount(); i++) {
			FileInfo0 fileDataEntity = entity.getList().get(i);
			if(dataEntity.getObjid()==fileDataEntity.getObjid()){
				currpag=i;
			}
		}
		return currpag;
	}
	
	public static boolean delete = false;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode==KeyEvent.KEYCODE_BACK) {
			if(delete){
				setResult(1);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public int getCurrpag() {
		return currpag;
	}
	
	public void refresh(String objid){
		for (int i = 0; i <entity.getCount(); i++) {
			if((entity.getList().get(i).getObjid()).equals(objid)){
				currpag--;
				
				delete = true;
				entity.getList().remove(i);
				//entity.setCount(entity.getCount()-1);
				if(entity.getCount()==0){
					finish();
					return;
				}
				menuShow = new PhotoPopMenuShow(this);
				page.setAdapter(new PhotoViewPager_Adapter(this, menuShow));
				FileInfo0 f=entity.getList().get(currpag);
				menuShow.setTitle(TimeUtils.getTime(f.getLastModified()),
						(currpag + 1) + "/" + entity.getCount());
				page.setCurrentItem(currpag);

				return;
			}
		}
		
		
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
	/** 分享到其他 */
	public void sendOther(String msg) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "fenxiang");
		intent.putExtra(Intent.EXTRA_TEXT, msg);
		startActivity(Intent.createChooser(intent, getTitle()));
	}
	
	/** 分享邮箱 */
	public void sendEmail(String msg1,String name) {
		 //建立Intent 对象
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
        //设置文本格式
        emailIntent.setType("plain/text"); 
        //设置对方邮件地址
      //  emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{[email={[email=email=%7B%C3%97%C3%97%C3%97%C3%97%C3%97%C3%97@gmail.com]××××××@gmail.com[/email]]××××××@gmail.com[/email]}); 
        //设置标题内容
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, name); 
        //设置邮件文本内容
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg1); 
        //启动一个新的ACTIVITY,"Sending mail..."是在启动这个ACTIVITY的等待时间时所显示的文字
        startActivity(Intent.createChooser(emailIntent, "FYX Sending mail..."));
	}
	/**
	*  //@param content要分享的字符串
	**/
	 @SuppressLint("NewApi")
	public void setClipBoard(String content) {
	  int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	  if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
	    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
	   android.content.ClipData clip = android.content.ClipData.newPlainText("label", content);
	   clipboard.setPrimaryClip(clip);
	  } else {
	   android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
	   clipboard.setText(content);
	  }
	 }
	 
	 @Override
	protected void onDestroy() {
		 menuShow.dis();
		super.onDestroy();
	}
	 
	 

}
