package com.chd.contacts.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.chd.base.Ui.ActiveProcess;
import com.chd.contacts.vcard.VCardIO;
import com.chd.base.backend.SyncTask;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;

import java.util.List;


public class Contacts extends ActiveProcess {
    /** Called when the activity is first created. */
	Handler mHandler = null;
	boolean isActive;
	VCardIO vcarIO = null;
	int mLastProgress;
	CheckBox mReplaceOnImport = null;
	Button importButton = null;
	Button exportButton = null;
	ProgressDialog progressDlg = null;
	private String importing = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_main);
        mHandler = new Handler();
        vcarIO = new VCardIO(this);
		// 显示进度条
		progressDlg = new ProgressDialog(this);
		progressDlg.setCancelable(false);
		progressDlg.setProgress(10000);

		importButton = (Button) findViewById(R.id.ImportButton);
		exportButton = (Button) findViewById(R.id.ExportButton);
		mReplaceOnImport = ((CheckBox) findViewById(R.id.ReplaceOnImport));
		
		OnClickListener listenImport = new OnClickListener() {
			public void onClick(View v) {
				if (vcarIO != null) {
					String fileName = ((EditText) findViewById(R.id.ImportFile))
							.getText().toString();
					// 更新进度
					progressDlg.show();
					importing = "正在导入,请稍后...";
					updateProgress(0);
					vcarIO.doImport(fileName, mReplaceOnImport.isChecked(),
							Contacts.this);
				}
			}
		};

		OnClickListener listenExport = new OnClickListener() {
			public void onClick(View v) {
				if (vcarIO != null) {
					String fileName = ((EditText) findViewById(R.id.ExportFile))
							.getText().toString();
					// 更新进度
					progressDlg.show();
					importing = "正在导出,请稍后...";
					updateProgress(0);
					vcarIO.doExport(fileName, Contacts.this);
				}
			}
		};
		importButton.setOnClickListener(listenImport);
		exportButton.setOnClickListener(listenExport);
    }
    /**
	 * 更新进度条
	 * 
	 * @param progress
	 *            进度
	 */
	@Override
	public void updateProgress(final int progress) {
		mHandler.post(new Runnable() {
			public void run() {
				progressDlg.setProgress(progress * 100);
				progressDlg.setMessage(importing + progress + "%");
				if (progress == 100) {
					progressDlg.cancel();
				}

			}
		});
	}

	public  void initdata()
	{
		SyncTask syncTask =new SyncTask(this, FTYPE.ADDRESS);
		//未备份文件 ==  backedlist . removeAll(localist);

		List<FileInfo0> cloudUnits=syncTask.getCloudUnits(0, 1000);

		for(FileInfo0 item:cloudUnits)
		{

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
	}
}