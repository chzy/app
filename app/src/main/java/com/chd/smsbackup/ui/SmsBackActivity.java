package com.chd.smsbackup.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.base.Ui.ActiveProcess;
import com.chd.smsbackup.newservice.ExportSms;
import com.chd.smsbackup.newservice.ImportSms;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;

public class SmsBackActivity extends ActiveProcess implements OnClickListener {

	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	private TextView mSmsNumber;
	private TextView mCloudNumber;
	private ImageView mIvSelect;
	private String smsPath;
	private ExportSms exportSms;
	private ImportSms importSms;


	private Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			dialog.dismiss();
			dismissDialog();
			try {
				switch (msg.what) {
					case 0:
						exportSms.getRemontCount(smsPath, handler);
						break;
					case 998:
						//本地短信量
						mSmsNumber.setText("" + msg.obj);
						break;
					case 999:
						//远程短信量
						mCloudNumber.setText("" + msg.obj);
						break;
				}
			} catch (Exception e) {
				Log.e(getClass().getName(),"退出页面空指针");
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_contact);
		smsPath = new ShareUtils(this).getSmsFile().getPath() + "/back.txt";
		exportSms = new ExportSms(this);
		importSms = new ImportSms(this);
		initTitle();
		initResourceId();
		initListener();
		newRequest();
	}

	private void newRequest() {
		dialog.show();
		exportSms.getCount(handler);
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
				importSms.ImpSMS(smsPath);
				break;
			case R.id.iv_select: // 一键备份
				// TODO
				setParMessage("正在上传");
				new Thread(new Runnable() {
					@Override
					public void run() {
						exportSms.ExpSMS(smsPath);
						exportSms.getRemontCount(smsPath, handler);
					}
				}).start();


				break;
		}
	}


}
