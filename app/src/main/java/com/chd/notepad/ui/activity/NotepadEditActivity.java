package com.chd.notepad.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.notepad.ui.db.DatabaseManage;
import com.chd.yunpan.R;
import com.chd.yunpan.utils.TimeUtils;

public class NotepadEditActivity extends Activity {
	
	public static final int CHECK_STATE = 0;
	public static final int EDIT_STATE = 1;
	public static final int ALERT_STATE = 2;
	
	private int state = -1;
	
	private ImageView mIvLeft;
	private TextView mTvCenter;
	private TextView mTvRight;
	private TextView title;
	private EditText content;
	private DatabaseManage dm = null;
	
	private String id = "";
	private String titleText = "";
	private String contentText = "";
	private String timeText = "";
	
	
	protected void onCreate(Bundle savedInstanceState){
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notepad_edit);
		
		initTitle();
		
		Intent intent = getIntent();
		state = intent.getIntExtra("state", EDIT_STATE);
		
		//赋值控件对象
		title = (TextView)findViewById(R.id.editTitle);
		content = (EditText)findViewById(R.id.editContent);
		content.setOnTouchListener(new OnTouchListener(){

			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				content.setSelection(content.getText().toString().length());
				
				return false;
			}
			
		});
		
		if(state == ALERT_STATE){//修改状态,赋值控件
			id = intent.getStringExtra("id");
			//titleText = intent.getStringExtra("title");
			long time = intent.getLongExtra("time",0);
			if (time<1000)
				time=System.currentTimeMillis();
			contentText = intent.getStringExtra("content");
			timeText = intent.getStringExtra("time");
			
			title.setText(TimeUtils.getTime(time));
			content.setText(contentText);
		}
		
		dm = new DatabaseManage(this);
	}

	private void initTitle() {
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		mTvCenter = (TextView) findViewById(R.id.tv_center);
		mTvRight = (TextView) findViewById(R.id.tv_right);

		mTvCenter.setText("添加心事");
		mTvRight.setText("发布");
		mIvLeft.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				finish();
			}
		});
		mTvRight.setOnClickListener(new EditCompleteListener());
	}
	
	/**
	 * 监听完成按钮
	 * @author mao
	 *
	 */
	public class EditCompleteListener implements OnClickListener {

		public void onClick(View v) {
			titleText = title.getText().toString();
			contentText = content.getText().toString();
			
			try{
				//dm.open();
				
				if(state == EDIT_STATE)//新增状态
					dm.insert(titleText, contentText);
				if(state == ALERT_STATE)//修改状态
					dm.update(Integer.parseInt(id), titleText, contentText);
				setResult(RESULT_OK);
				//dm.close();
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			/*Intent intent = new Intent();
			intent.setClass(NotepadEditActivity.this, NotepadActivity.class);
			intent.putExtra("needsync",(state==EDIT_STATE || state==ALERT_STATE));
			NotepadEditActivity.this.startActivity(intent);*/
			finish();
		}
		
	}
}
