package com.chd.photo.ui;


import com.chd.base.Ui.ActiveProcess;

//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.chd.MediaMgr.utils.MediaFileUtil;
//import com.chd.base.Ui.DownListActivity;
//import com.chd.base.backend.SyncTask;
//import com.chd.contacts.vcard.StringUtils;
//import com.chd.photo.adapter.PicEditAdapter;
//import com.chd.photo.entity.PicEditBean;
//import com.chd.photo.entity.PicEditItemBean;
//import com.chd.photo.entity.PicInfoBean;
//import com.chd.proto.FTYPE;
//import com.chd.proto.FileInfo0;
//import com.chd.yunpan.R;
//import com.chd.yunpan.share.ShareUtils;
//import com.chd.yunpan.utils.DensityUtil;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
public class PicEditActivity extends ActiveProcess {
//
//	private final String TAG = this.getClass().getName();
//	SyncTask syncTask;
//	List<FileInfo0> cloudUnits;
//	private ImageView mIvLeft;
//	private TextView mTvCenter;
//	private TextView mTvRight;
//	private ListView mLvPic;
//	private View mLvPicEditDel;
//	private View mLvPicEditUpDown;
//	private View mLvPicEditView;
//	private int month, year;
//	private List<PicEditBean> mPicList = new ArrayList<PicEditBean>();
//	private List<PicInfoBean> mPicList0;//数组对象传递到 这个变量里面了
//	private PicEditAdapter picEditAdapter;
//	private boolean bIsUbkList;
//	private String path;
//	private Handler handler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			try {
//				if (msg.what == 998) {
//					//多文件上传
//					processMsg(msg);
//				} else if (msg.what == 997) {
//					//多文件下载
//					mTvRight.performClick();
//					Toast.makeText(PicEditActivity.this, "文件保存本地成功", Toast.LENGTH_SHORT).show();
//				} else if (msg.what == 996) {
//					processMsg(msg);
//				} else {
//					if (mPicList != null) {
//						Collections.sort(mPicList);
//					}
//					if (mPicList != null) {
//						picEditAdapter.setData(mPicList);
//					} else {
//						picEditAdapter.notifyDataSetChanged();
//					}
//				}
//			} catch (Exception E) {
//				Log.e(getClass().getName(), "退出页面空指针");
//			}
//		}
//	};
//
//	private void processMsg(Message msg) {
//		//删除
//		ArrayList<Integer> posList = (ArrayList<Integer>) msg.obj;
//		if (posList.size() == 0) {
//			if (msg.what == 998)
//				toastMain("上传成功");
//			else if (msg.what == 996) {
//				toastMain("删除成功");
//			}
//		} else {
//			if (msg.what == 998)
//				toastMain("上传失败");
//			else if (msg.what == 996) {
//				toastMain("删除失败");
//			}
//		}
//		if (!(posList.size() == selectItem.size())) {
//			int i = 0;
//			for (Integer index :
//					posList) {
//				int pos = index - i;
//				selectItem.remove(pos);
//				i++;
//			}
//			int count = mPicList.size();
//			ArrayList<PicEditBean> items = new ArrayList<>();
//			for (int j = 0; j < count; j++) {
//
//				PicEditBean bean = mPicList.get(j);
//				if (bean.getList() != null) {
//					bean.getList().removeAll(selectItem);
//					if(bean.getList().isEmpty()){
//						items.add(bean);
//					}
//				}
//			}
//			mPicList.removeAll(items);
//			setResult(RESULT_OK);
//			if (mPicList != null) {
//				Collections.sort(mPicList);
//			}
//			picEditAdapter.notifyDataSetChanged();
//		}
//	}
//
//
//	private Button mBtnDown;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_pic_edit);
//		bIsUbkList = getIntent().getBooleanExtra("ubklist", false);
//		month = getIntent().getIntExtra("month", -1);
//		mPicList0 = (List) getIntent().getSerializableExtra("listUnits");
//		path = new ShareUtils(this).getPhotoFile().getPath();
//		initTitle();
//		initResourceId();
//		initListener();
//		picEditAdapter = new PicEditAdapter(PicEditActivity.this, mPicList);
//		mLvPic.setAdapter(picEditAdapter);
////		onNewThreadRequest();
//		syncTask = new SyncTask(this, FTYPE.PICTURE);
//		initData();
//	}
//
//
//	private void initData() {
//		Map<String, List<PicEditItemBean>> tmpDayMap = new HashMap<String, List<PicEditItemBean>>();
//		FileInfo0 info0 = null;
//		for (PicInfoBean item : mPicList0) {
//			//if (bIsUbkList)
//			String tmpDay = item.getDay();
//			PicEditItemBean picInfoBean = new PicEditItemBean();
//			if (item.getSysid() > 0) {
//				//info0 = syncTask.queryLocalInfo(item.getSysid());
//				if (info0 != null)
//					picInfoBean.setUrl("file://" + info0.getFilePath());
//			} else
//				picInfoBean.setUrl(item.getUrl());
//
//			picInfoBean.setSelect(false);
//			picInfoBean.setbIsUbkList(bIsUbkList);
//			//picInfoBean.setFileInfo0(info);
//
//			if (tmpDayMap.get(tmpDay) != null) {
//				tmpDayMap.get(tmpDay).add(picInfoBean);
//			} else {
//				List<PicEditItemBean> dayItemBeans = new ArrayList<PicEditItemBean>();
//				dayItemBeans.add(picInfoBean);
//				tmpDayMap.put(tmpDay, dayItemBeans);
//			}
//		}
//
//
//		if (tmpDayMap.size() > 0) {
//
//			for (Map.Entry<String, List<PicEditItemBean>> entryDay : tmpDayMap.entrySet()) {
//				PicEditBean picBean = new PicEditBean(String.valueOf(entryDay.getKey()), entryDay.getValue());
//				picBean.setbIsUbkList(bIsUbkList);
//				mPicList.add(picBean);
//			}
//		}
//
//		handler.sendEmptyMessage(0);
//	}
//
//	private void initListener() {
//		mIvLeft.setOnClickListener(this);
//		mTvRight.setOnClickListener(this);
//		mTvRight.setTag(false);
//		mLvPicEditDel.setOnClickListener(this);
//		mLvPicEditUpDown.setOnClickListener(this);
//		mBtnDown.setOnClickListener(this);
//	}
//
//	private void initResourceId() {
//		mLvPic = (ListView) findViewById(R.id.lv_pic_edit);
//		mLvPicEditDel = findViewById(R.id.lv_pic_edit_del);
//		mLvPicEditView = findViewById(R.id.lv_pic_edit_layout);
//		mLvPicEditUpDown = findViewById(R.id.lv_pic_edit_updown);
//		mBtnDown = (Button) findViewById(R.id.pic_btn_down);
//
//		TextView textView = (TextView) findViewById(R.id.lv_pic_edit_updown_text);
//		if (bIsUbkList) {
//			textView.setText("上传");
//		} else {
//			textView.setText("下载");
//		}
//	}
//
//	private void initTitle() {
//		mIvLeft = (ImageView) findViewById(R.id.iv_left);
//		mTvCenter = (TextView) findViewById(R.id.tv_center);
//		mTvRight = (TextView) findViewById(R.id.tv_right);
//
//		mTvCenter.setText("照片");
//		mTvRight.setText("编辑");
//	}
//
//	List<PicEditItemBean> selectItem;
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//			case R.id.pic_btn_down:
//				//任务列表
//				Intent intent = new Intent(PicEditActivity.this, DownListActivity.class);
//				startActivity(intent);
//				break;
//
//			case R.id.iv_left:
//				finish();
//				break;
//			case R.id.tv_right:
//				boolean bTag = (Boolean) mTvRight.getTag();
//				mLvPicEditView.setVisibility(bTag ? View.GONE : View.VISIBLE);
//				mTvRight.setText(bTag ? "编辑" : "取消");
//				mTvRight.setTag(!bTag);
//				if (!bTag) {
//					mLvPic.setPadding(0,0,0, DensityUtil.dip2px(this,40));
//					picEditAdapter.setEdit(true);
//				} else {
//					mLvPic.setPadding(0,0,0, 0);
//					picEditAdapter.setEdit(false);
//				}
//
////				handler.sendEmptyMessage(0);
//				break;
//			case R.id.lv_pic_edit_del: {
//				//多选删除
//				selectItem = new ArrayList<>();
//				for (PicEditBean picEditBean : mPicList) {
//					for (PicEditItemBean picEditItemBean : picEditBean.getList()) {
//						if (!picEditItemBean.isSelect()) {
//							continue;
//						}
//						selectItem.add(picEditItemBean);
//					}
//				}
//				ArrayList<FileInfo0> info0s = new ArrayList<>();
//				for (PicEditItemBean bean :
//						selectItem) {
//					FileInfo0 fileInfo0 = new FileInfo0();
//					if (StringUtils.isNullOrEmpty(bean.getUrl())) {
//						continue;
//					}
//					int idx = bean.getUrl().indexOf("://");
//					if (idx < 0) {
//						Log.e(TAG, "error file path fail!!!!");
//						continue;
//					}
//					idx += 3;
//					String uri = bean.getUrl().substring(idx);
//					if (bean.isbIsUbkList()) {
//						fileInfo0.setFilePath(uri);
//						//fileInfo0.setObjid(MediaFileUtil.getFnameformPath(uri));
//					}/*else {
//						fileInfo0.setObjid(uri);
//					}*/
//					fileInfo0.setObjid(MediaFileUtil.getFnameformPath(uri));
//					info0s.add(fileInfo0);
//				}
//				//syncTask.delList(info0s, this, handler, bIsUbkList);
//			}
//			break;
//			case R.id.lv_pic_edit_updown: {
//
//				selectItem = new ArrayList<>();
//				for (PicEditBean picEditBean : mPicList) {
//
//					for (PicEditItemBean picEditItemBean : picEditBean.getList()) {
//						if (!picEditItemBean.isSelect()) {
//							continue;
//						}
//						selectItem.add(picEditItemBean);
//					}
//				}
//				ArrayList<FileInfo0> info0s = new ArrayList<>();
//				for (PicEditItemBean bean :
//						selectItem) {
//					FileInfo0 fileInfo0 = new FileInfo0();
//					if (StringUtils.isNullOrEmpty(bean.getUrl())) {
//						Log.e(TAG, "empty path fail!!!!");
//						continue;
//					}
//					int idx = bean.getUrl().indexOf("://");
//					if (idx < 0) {
//						Log.e(TAG, "error file path fail!!!!");
//						continue;
//					}
//					idx += 3;
//					String uri = bean.getUrl().substring(idx);
//					if (bean.isbIsUbkList()) {
//						fileInfo0.setFilePath(uri);
//						fileInfo0.setObjid(MediaFileUtil.getFnameformPath(uri));
//					} else {
//						if (StringUtils.isNullOrEmpty(fileInfo0.getFilePath())) {
//							fileInfo0.setFilePath(path + "/" + uri);
//						}
//						fileInfo0.setObjid(uri);
//					}
//					info0s.add(fileInfo0);
//				}
//				if (bIsUbkList) {
//					//syncTask.uploadList(info0s, this, handler);
//				} else {
//					//	syncTask.downloadList(info0s, this, handler);
//				}
//			}
//			break;
//			default:
//				break;
//		}
//	}
//
//
}
