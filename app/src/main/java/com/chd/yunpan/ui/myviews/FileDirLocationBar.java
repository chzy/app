package com.chd.yunpan.ui.myviews;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;

import com.chd.Entity.FilesListEntity;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
/*import com.chd.yunpan.parse.entity.FilePathEntity;*/

import java.util.ArrayList;
import java.util.List;

public class FileDirLocationBar implements OnClickListener {

	

	List<Button> buttons = new ArrayList<Button>();

	private Context context = null;

	private ViewGroup viewGroup = null;

	private HorizontalScrollView hor = null;

	

	public boolean isRootDir = true;
	
	//文件路径
	/*private List<FilePathEntity> filePathEntities ;*/

	/**
	 * @param context
	 *            所在的上下文
	 * @param viewGroup
	 *            所使用的布局
	 * @param horizontalScrollView
	 *            需要一个horizontalScrollView装载viewGroup
	 * 
	 * 
	 *            需要的布局: <HorizontalScrollView android:id="@+id/hor"
	 *            android:layout_width="match_parent"
	 *            android:layout_height="40dp" android:scrollbars="none" >
	 * 
	 *            <FrameLayout android:id="@+id/locationID"
	 *            android:layout_width="wrap_content"
	 *            android:layout_height="40dp" android:orientation="horizontal"
	 *            > </FrameLayout> </HorizontalScrollView>
	 */
	public FileDirLocationBar(Context context, ViewGroup viewGroup,
			HorizontalScrollView horizontalScrollView,List</*FilePathEntity*/FilesListEntity> filePathEntities) {

		this.context = context;

		
		this.viewGroup = viewGroup;
		
		viewGroup.removeAllViews();
		
		this.hor = horizontalScrollView;

		//this.filePathEntities = filePathEntities;

		for (int i = 0; i < filePathEntities.size(); i++) {
			Message msg = new Message();
			msg.obj = filePathEntities.get(i);
			msg.what = i;
			handler.sendMessageDelayed(msg, i*50);
		}
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what!=-1){
				//addButton((FilePathEntity)msg.obj);
				handler.sendEmptyMessageDelayed(-1, 30);
			}else {
				hor.scrollTo(buttons.get(buttons.size()-1).getRight(), 0);
			}
		}
	};
	
	public void addBtn(/*FilePathEntity*/ FileInfo0 item){
		Message msg = new Message();
		msg.obj = item;
		msg.what = 1;
		handler.sendMessage(msg);
	}
	
	
	private void addButton(/*FilePathEntity*/FilesListEntity item) {
		// int index = item.getfPath().lastIndexOf("/") + 1;
		// String displayName = item.getfPath().substring(index);
		
	/*	Button b = createNewButton(item.getName());
		b.setOnClickListener(this);
		addButton2View(b);
		buttons.add(b);
		b.setTag(item);*/
	}

	
	
	private void addButton2View(Button b) {
		FrameLayout.LayoutParams params = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.LEFT);
		if(buttons.size()==0){
			params.leftMargin = -50;
		}else {
			params.leftMargin = buttons.get(buttons.size()-1).getRight() - 50;
		}

		b.setLayoutParams(params);
		viewGroup.addView(b,0);
	}

	
	private Button createNewButton(String title) {

		Button b = new Button(context);
		b.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		b.setSingleLine();
		b.setText(title);
		b.setBackgroundResource(R.drawable.location_btn_last);
		return b;
	}

	@Override
	public void onClick(View v) {
	/*	FilePathEntity item = (FilePathEntity) v.getTag();
		if(onFilePathChange!=null){
			onFilePathChange.filePathChange(item.getCid());
		}
		int position = -1;
		for (int i = 0; i < buttons.size(); i++) {
			if(v==buttons.get(i)){
				position = i;
			}
		}
		List<Button> canrms = new ArrayList<Button>();
		for (int i = position+1; i < buttons.size(); i++) {
			canrms.add(buttons.get(i));
		}
		
		for (int i = 0; i < canrms.size(); i++) {
			buttons.remove(canrms.get(i));
			viewGroup.removeView(canrms.get(i));
		}*/
	}
	
	private OnFilePathChange onFilePathChange = null;

	public void setOnFilePathChange(OnFilePathChange onFilePathChange) {
		this.onFilePathChange = onFilePathChange;
	}

	public interface OnFilePathChange {
		void filePathChange(int cid);
	}

	

}
