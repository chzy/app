package com.chd.contacts.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.FilelistEntity;
import com.chd.base.Ui.ActiveProcess;
import com.chd.base.backend.SyncTask;
import com.chd.contacts.adapter.ContactAdapter;
import com.chd.contacts.adapter.ContactBean;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends ActiveProcess implements OnClickListener,OnItemClickListener {

	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	private TextView mSmsNumber;
	private TextView mCloudNumber;
	private ImageView mIvSelect;
	private ListView mLvContact;

	private List<ContactBean> mContactList = new ArrayList<ContactBean>();

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mLvContact.setAdapter(new ContactAdapter(ContactActivity.this,
					mContactList));
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_contact);

		initTitle();
		initResourceId();
		initListener();
		initData();
	}

	private void initData() {


		SyncTask syncTask =new SyncTask(this, FTYPE.ADDRESS);

		List<FileInfo0> cloudUnits=syncTask.getCloudUnits(0, 1000);
		if (cloudUnits==null)
		{
			System.out.print("query remote failed");
		}
		FilelistEntity filelistEntity=syncTask.analyUnits(cloudUnits);
		cloudUnits.clear();
		cloudUnits=null;
		List<FileLocal> fileLocals=filelistEntity.getLocallist();
		cloudUnits=filelistEntity.getBklist();
		//显示的时候过滤文件类型

		for(FileInfo0 item:cloudUnits)
		{
			//FileInfo0 item=new FileInfo0(finfo);
			//已备份文件
			if (syncTask.haveLocalCopy(item))
			{
				String path=item.getFilePath();
			}
			else
			{
				String savepath="/sdcard/ddd";
				item.setFilePath(savepath);
				//param1  object ,param2 progressBar, param 3  beeque
				//syncTask.download(item,null,false);
			}
		}

		//显示未备份文件
		for(FileLocal fileLocal:fileLocals)
		{


			if (fileLocal.bakuped)
				continue;

			FileInfo0 info0 =syncTask.queryLocalInfo(fileLocal.sysid);//本地文件信息
			syncTask.upload(info0, null,false);
		}

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
		handler.sendEmptyMessage(0);

	}

	private void initResourceId() {
		mSmsNumber = (TextView) findViewById(R.id.tv_sms_number);
		mCloudNumber = (TextView) findViewById(R.id.tv_cloud_number);
		mIvSelect = (ImageView) findViewById(R.id.iv_select);
		mLvContact = (ListView) findViewById(R.id.lv_contact);

		mSmsNumber.setText("135");
		mCloudNumber.setText("135");
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
		mTvRight.setOnClickListener(this);
		mIvSelect.setOnClickListener(this);
		mLvContact.setOnItemClickListener(this);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);
		mTvRight = (TextView) findViewById(R.id.tv_right);

		mTvCenter.setText("联系人备份");
		mTvRight.setText("回收");
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_left:
			finish();
			break;
		case R.id.tv_right: // 回收
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
		Toast.makeText(this, "点击了条目"+arg2, Toast.LENGTH_SHORT).show();
	}
}
