package com.chd.yunpan.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.MediaMgr.utils.MediaFileUtil;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.net.DeleteRun;
import com.chd.yunpan.net.NetworkUtils;
import com.chd.yunpan.parse.entity.DeleteFileEntity;
import com.chd.yunpan.ui.fragment.PhotoListFragment;
import com.chd.yunpan.utils.TimeUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class PhotoDeleteDialog {
	private Dialog d = null;

	private View view = null;

	private View confirm = null;

	private Button cancel = null;

	private Context context = null;
	
	private List<FileInfo0> entities=null;
	private PhotoListFragment fragment=null;

	private ListView lv;
	private List<FileInfo0> pictures;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what==-1) {
				Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
				return;
			}
			DeleteFileEntity Delentity = (DeleteFileEntity) msg.obj;
			
			if (Delentity.isState()) {
				Toast.makeText(context, "删除完成", Toast.LENGTH_SHORT).show();
				
				fragment.refresh();
				
			} else {
				Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
			}
			
			sum--;
			if(sum<=0){
				if(d!=null){
					disDialog();
					fragment.dis();
					fragment.refreshWithDataChange();
				}
			}
		}
	};

	public PhotoDeleteDialog(Context context, List<FileInfo0> entities,PhotoListFragment fragment) {
		super();
		this.fragment=fragment;
		this.context = context;
		this.entities = entities;
		d = new Dialog(context);
		pictures=new ArrayList<FileInfo0>();
		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i).isSelected()) {
				pictures.add(entities.get(i));
			}
		}
		view = View.inflate(context, R.layout.photo_delete, null);

		lv = (ListView) view.findViewById(R.id.photo_delete_lv);
		MyAdapter adapter = new MyAdapter();

		lv.setAdapter(adapter);

		d.setContentView(view);

		confirm = d.findViewById(R.id.photo_delete_lin);

		cancel = (Button) d.findViewById(R.id.photo_delete_cancel);

		init();
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (pictures.size() > 3) {
				return 1;
			} else {
				return pictures.size();
			}

		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = View.inflate(context, R.layout.photo_delete_item,
					null);
			TextView textView = (TextView) view
					.findViewById(R.id.photo_delete_title);
			ImageView imv = (ImageView) convertView
					.findViewById(R.id.photo_delete_item_image);
			TextView name = (TextView) convertView
					.findViewById(R.id.photo_delete_item_name);
			TextView time = (TextView) convertView
					.findViewById(R.id.photo_delete_item_time);
			TextView sizes = (TextView) convertView
					.findViewById(R.id.photo_delete_item_sizes);
			TextView size = (TextView) convertView
					.findViewById(R.id.photo_delete_item_size);
			ImageLoader loaderUtil = ImageLoader.getInstance();
			loaderUtil.displayImage(pictures.get(position).getFilename(), imv);
			if (pictures.size() == 1) {
				textView.setText("确定要将这个文件删除？");
				name.setText(pictures.get(position).getFilename());
				sizes.setText("");
				size.setText(MediaFileUtil.convertStorage(pictures.get(position).getFilesize()));
				time.setText(TimeUtils.getTime(pictures.get(position).getLastModified()));
			} else if (pictures.size() < 4 && pictures.size() > 1) {
				textView.setText("确定要将这" + pictures.size() + "项删除？");
				name.setText(pictures.get(position).getFilename());
				size.setText("");
				sizes.setText(MediaFileUtil.convertStorage(pictures.get(position).getFilesize()));
				time.setText("");
			} else {
				textView.setText("确定要将这些文件删除？");
				name.setText(pictures.get(0).getFilename() + "等" + pictures.size()
						+ "项");
				sizes.setText("");
				size.setText("");
				time.setText("");
			}
			return convertView;
		}
	}
	
	private int sum;

	private void init() {

		Window dialogWindow = d.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		lp.width = LayoutParams.MATCH_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		// lp.x = 0;
		// lp.y = getWindowHeight();
		dialogWindow
				.setBackgroundDrawableResource(R.drawable.background_dialog);

		final int height = lp.height;

		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(NetworkUtils.isNetworkAvailable(context)){
				showDialog();
				sum = pictures.size();
				/*for (int i = 0; i < pictures.size(); i++)*/
				{
					//String s = pictures.get(i).getFid()+"";
					//添加删除事件
						// if (items.get(i).getCid() == null) {
						DeleteRun deleteRun = new DeleteRun(/*1, pictures.get(i)
								.getFid(), 0,*/ context, handler,pictures
								/*pictures.get(i).getPid()*/);
						new Thread(deleteRun).start();
						// } else {
						// String fid = String.valueOf(items.get(i).getFid());
						// String cid = String.valueOf(items.get(i).getCid());
						// DeleteRun deleteRun = new DeleteRun(2, fid, cid,
						// context, handler, items.get(i).getPid());
						// new Thread(deleteRun).start();
						// }
					
				}
				dialogAnimation(d, view, height, getWindowHeight(), true);
				}else {
					Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();
				}

			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogAnimation(d, view, height, getWindowHeight(), true);
				for (int i = 0; i < entities.size(); i++) {
					entities.get(i).setIsChecked( false);
				}
				fragment.refresh();
			}
		});
		d.show();
		dialogAnimation(d, view, getWindowHeight(), height, false);

	}

	Dialog dialog = null;

	public void showDialog() {

		dialog = new Dialog(context);
		Window dialogWindow = dialog.getWindow();
		dialogWindow
				.setBackgroundDrawableResource(R.drawable.background_dialog);
		dialog.setContentView(R.layout.photo_delete_dialog);
		ImageView imageView = (ImageView) dialog
				.findViewById(R.id.photo_delete_diaog_image);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				disDialog();
			}
		});
		dialog.show();
	}

	public void disDialog() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	private int getWindowHeight() {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		return dm.widthPixels;
	}

	private void dialogAnimation(final Dialog d, View v, int from, int to,
			boolean needDismiss) {

		Animation anim = new TranslateAnimation(0, 0, from, to);
		anim.setFillAfter(true);
		anim.setDuration(600);
		if (needDismiss) {
			anim.setAnimationListener(new AnimationListener() {

				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					d.dismiss();
				}
			});

		}

		v.startAnimation(anim);
	}

	private OnConfirmListener onConfirmListener = null;

	public interface OnConfirmListener {
		void confirm();
	}

	public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
		this.onConfirmListener = onConfirmListener;
	}

}
