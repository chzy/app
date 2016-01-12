package com.chd.smsbackup.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.TClient;
import com.chd.base.backend.SyncTask;
import com.chd.contacts.adapter.ContactBean;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.smsbackup.service.ExportSms;
import com.chd.smsbackup.service.ImportSms;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SmsActivity extends Activity implements OnClickListener,OnItemClickListener {

	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	private TextView mSmsNumber;
	private TextView mCloudNumber;
	private ImageView mIvSelect;
	private ListView mLvContact;
	private Button  backup;
	private Button resore;

	private List<ContactBean> mContactList = new ArrayList<ContactBean>();

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			/*mLvContact.setAdapter(new ContactAdapter(SmsActivity.this,
					mContactList));*/
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sms);

		initTitle();
		initResourceId();
		initListener();
		initData();
	}

	private void initData() {
		// 模拟数据
		ContactBean contactBean0 = new ContactBean("2015年10月", "3");
		ContactBean contactBean1 = new ContactBean("2015年8月", "3");
		ContactBean contactBean2 = new ContactBean("2015年6月", "5");
		ContactBean contactBean3 = new ContactBean("2015年12月", "31");
		ContactBean contactBean4 = new ContactBean("2015年5月", "4");
		mContactList.add(contactBean0);
		mContactList.add(contactBean1);
		mContactList.add(contactBean2);
		mContactList.add(contactBean3);
		mContactList.add(contactBean4);

		SyncTask syncTask =new SyncTask(this, FTYPE.SMS);

		List<FileInfo0> cloudUnits=syncTask.getCloudUnits(0, 1000);
		if (cloudUnits==null)
		{
			System.out.print("query remote failed");
		}

		for(FileInfo item:cloudUnits)
		{
			//这里得到 文件行数 ,用来显示有多少条短信备份了
			try {
				String value= TClient.getinstance().queryAttribute(item.objid,item.getFtype(),"lines");
				int lines=Integer.valueOf(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
			{
				FileInfo0 item1=new FileInfo0(item);
				String savepath="/sdcard/ddd";
				item1.setFilePath(savepath);
				//param1  object ,param2 progressBar, param 3  beeque
				//syncTask.download(item,null,false);
			}
		}
		handler.sendEmptyMessage(10);
	}

	private void initResourceId() {
		/*mSmsNumber = (TextView) findViewById(R.id.tv_sms_number);
		mCloudNumber = (TextView) findViewById(R.id.tv_cloud_number);
		mIvSelect = (ImageView) findViewById(R.id.iv_select);
		mLvContact = (ListView) findViewById(R.id.lv_contact);

		mSmsNumber.setText("135");
		mCloudNumber.setText("135");*/
		backup =(Button) findViewById(R.id.bakup);
		resore=(Button) findViewById(R.id.restor);
	}

	private void initListener() {
		backup.setOnClickListener(this);
		resore.setOnClickListener(this);
		/*mIvSelect.setOnClickListener(this);
		mLvContact.setOnItemClickListener(this);*/
	}

	private void initTitle() {
		/*mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);
		mTvRight = (TextView) findViewById(R.id.tv_right);

		mTvCenter.setText("联系人备份");
		mTvRight.setText("回收");*/
	}

	@Override
	public void onClick(View view) {
		ShareUtils shareUtils =new ShareUtils(this);
		String file=shareUtils.getStorePathStr()+ new Random().nextInt(30) +"smsback.bak";
		 //file=shareUtils.getStorePathStr()+17 +"smsback.bak";

		switch (view.getId()) {
		case R.id.bakup:
			ExportSms exportSms=new ExportSms(this);
			try {
				exportSms.CreateTxtfile(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//finish();
			break;
		case R.id.restor:

			ImportSms importSms=new ImportSms(this);
			importSms.InsertSMS(file);
			// TODO
			break;
		case R.id.iv_select: // 选择备份
			// TODO
			break;
		}
	}
	
	/**列表点击**/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		//TODO
		Toast.makeText(this, "点击了条目" + arg2, Toast.LENGTH_SHORT).show();
	}
}
