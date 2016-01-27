package com.chd.smsbackup.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chd.base.Ui.ActiveProcess;
import com.chd.contacts.entity.ContactBean;
import com.chd.smsbackup.service.ExportSms;
import com.chd.smsbackup.service.ImportSms;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;

import java.util.ArrayList;
import java.util.List;

public class SmsBackActivity extends ActiveProcess implements OnClickListener {

	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	private TextView mSmsNumber;
	private TextView mCloudNumber;
	private ImageView mIvSelect;
	private ListView mLvContact;
	private String smsPath;
	private ExportSms exportSms;
	private ImportSms importSms;

	private List<ContactBean> mContactList = new ArrayList<ContactBean>();

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
				switch (msg.what){
					case 0:
						new Thread(new Runnable() {
							@Override
							public void run() {
								final int count = exportSms.getCount();
								handler.post(new Runnable() {
									@Override
									public void run() {
										mSmsNumber.setText(count+"");
									}
								});
							}
						}).start();
						new Thread(new Runnable() {
							@Override
							public void run() {
								final int size=importSms.getCount(smsPath);
								handler.post(new Runnable() {
									@Override
									public void run() {
										mCloudNumber.setText(size+"");
									}
								});
							}
						}).start();

						break;
				}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_contact);
		smsPath=new ShareUtils(this).getSmsFile().getPath()+"/message.xml";
		exportSms=new ExportSms(this);
		importSms=new ImportSms(this);
		initTitle();
		initResourceId();
		initListener();
		newRequest();
	}

	private void newRequest() {

		new Thread(new Runnable() {
			@Override
			public void run() {

				exportSms.download(smsPath,SmsBackActivity.this);
				handler.sendEmptyMessage(0);


			}
		}).start();



	}

	private void initResourceId() {
		mSmsNumber = (TextView) findViewById(R.id.tv_sms_number);
		mCloudNumber = (TextView) findViewById(R.id.tv_cloud_number);
		mIvSelect = (ImageView) findViewById(R.id.iv_select);
	}

	private void initListener() {
		mIvLeft.setOnClickListener(this);
		mTvRight.setOnClickListener(this);
		mIvSelect.setOnClickListener(this);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);
		mTvRight = (TextView) findViewById(R.id.tv_right);

		mTvCenter.setText("短信备份");
		mTvRight.setText("一键恢复");
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_left:
			finish();
			break;
		case R.id.tv_right: // 一键恢复
			// TODO
			setParMessage("正在恢复");
			new Thread(new Runnable() {
				@Override
				public void run() {
					importSms.testInsertSMS(smsPath);
				}
			}).start();
			break;
		case R.id.iv_select: // 一键备份
			// TODO
			setParMessage("正在上传");
			new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						boolean b = exportSms.createXml(smsPath);

						if(b){
							final int size=importSms.getCount(smsPath);
							handler.post(new Runnable() {
								@Override
								public void run() {
									mCloudNumber.setText(size+"");
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();


			break;
		}
	}
	

}
