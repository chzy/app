package com.chd.yunpan.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.db.DBManager;
import com.chd.yunpan.ui.adapter.DownAndUpAdapter;
import com.chd.yunpan.ui.dialog.JDDialogDownedStop;
import com.chd.yunpan.utils.FileOpenUtils;

import java.io.File;
import java.util.List;

public class DownloadED extends Activity implements OnClickListener {
	private View back;
	private Intent intent;
	private ListView lv;
	private DBManager sq;
	private DownAndUpAdapter adapter;
	private List<FileInfo0> files;
	private View nullShow;

	private View stop;
	
	private TextView tv = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_ed);
		sq = new DBManager(this);
		tv = (TextView) findViewById(R.id.title);
		sq.open();
		getFile();
		setView();
	}
	
	public List<FileInfo0> getFileDataDBEntities(){
		return files;
	}

	public void getFile() {
		int type = getIntent().getIntExtra("type", 0);
		switch (type) {
		case 0:
			tv.setText("上传/下载");
			files = sq.getDownloadingFiles();
			files.addAll(sq.getUpLoadingFiles());
			break;

		case 1:
			tv.setText("下载文件");
			files = sq.getDownloadedFiles();
			break;
		case 2:
			tv.setText("上传");
			files = sq.getUpLoadedFiles();
			break;
		}
	}

	public void refresh() {
		getFile();
		adapter = new DownAndUpAdapter(getApplicationContext(), files);
		lv.setAdapter(adapter);
	}

	public void setView() {
		stop = findViewById(R.id.trans_lay_stop);
		stop.setOnClickListener(this);
		nullShow = findViewById(R.id.downlond_null_show);
		lv = (ListView) findViewById(R.id.downlond_listview);
		adapter = new DownAndUpAdapter(getApplicationContext(), files);
		lv.setAdapter(adapter);
		if (files != null) {
			lv.setVisibility(View.VISIBLE);
			nullShow.setVisibility(View.GONE);
		} else {
			lv.setVisibility(View.GONE);
			nullShow.setVisibility(View.VISIBLE);
		}
		back = findViewById(R.id.downlond_back);
		back.setOnClickListener(this);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(
						getApplicationContext(),
						files.get(arg2).getFilePath() + "vv"
								/*+ files.get(arg2).getN()*/, Toast.LENGTH_SHORT).show();
				if (files.get(arg2).getFilePath() != null) {
					File file = new File(files.get(arg2).getFilePath());
					
					if (file.exists()) {
						if(file.isFile()){
							try {
								Intent intent = FileOpenUtils.openFile(files.get(
										arg2).getFilePath());
								startActivity(intent);
							} catch (Exception e) {
								Toast.makeText(DownloadED.this, "文件类型不支持", Toast.LENGTH_SHORT)
										.show();
							}
						}else {
							/*File f1 = new File(*//*file.getAbsolutePath()+"/"+files.get(arg2).getN()*//*);
							try {
								Intent intent = FileOpenUtils.openFile(f1.getAbsolutePath());
								startActivity(intent);
							} catch (Exception e) {
								Toast.makeText(DownloadED.this, "文件类型不支持", 0)
										.show();
							}*/
							//throw new Exception("unknow file ,dis dirctory");
							assert(false);
						}
					} else {
						Toast.makeText(DownloadED.this, "文件删除已删除", Toast.LENGTH_SHORT).show();
					}
				}

			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.downlond_back:
			DownloadED.this.finish();
			break;
		case R.id.trans_lay_stop:
			JDDialogDownedStop stop = new JDDialogDownedStop(this);
			stop.showMyDialog();

			break;

		default:
			break;
		}

	}
}
