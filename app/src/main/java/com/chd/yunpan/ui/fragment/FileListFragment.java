package com.chd.yunpan.ui.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.chd.Entity.FilesListEntity;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.db.DBManager;
import com.chd.yunpan.net.RequestParam;
import com.chd.yunpan.parse.entity.DeleteFileEntity;
import com.chd.yunpan.parse.entity.FileDirDataEntity;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.ui.DownloadED;
import com.chd.yunpan.ui.MainFragmentActivity;
import com.chd.yunpan.ui.PhotoShowActivity;
import com.chd.yunpan.ui.UpLoadActivity;
import com.chd.yunpan.ui.adapter.FileListViewAdapter;
import com.chd.yunpan.ui.fragment.popupwindow.MenuMore;
import com.chd.yunpan.ui.fragment.popupwindow.PopMenu;
import com.chd.yunpan.utils.FileOpenUtils;
import com.chd.yunpan.utils.ToastUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

/*import com.chd.yunpan.parse.entity.FileDataEntity;*/
/*import com.chd.yunpan.parse.entity.FilePathEntity;
import com.chd.yunpan.parse.entity.FilesListEntity;*/

public class FileListFragment extends BaseFragment implements OnClickListener {

	private FilesListEntity<FileInfo0> entity ;
	
	private PullToRefreshListView listView ;
	
	private FileListViewAdapter adapter = null;
	
	private int offset = 0;
	
	public int isPop = 0;
	
	//最后一个后，是否在加载
	private boolean lastting =  false;
	
	private PopMenu popMenu ;
	
	private View upLoad;
	private View share;
	private View download;
	private View more_dis;
	
	private com.chd.yunpan.ui.fragment.popupwindow.MenuMore menuMore;
	private TextView nor;
	private TextView dis;
	private boolean menuMoreShow = false;
	

	
	//文件导航
	//private FileDirLocationBar bar2 = null;
	private ViewGroup group;
	private HorizontalScrollView horizontalScrollView;

	private  ShareUtils shareUtils;
	private DBManager dbManager;



	@Override
	public View createView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.filelist_fragment_layout, null);
	}

	@Override
	public void onResume() {
		super.onResume();
		if(dbManager.getDownloadingFiles().size()>0||dbManager.getUpLoadingFiles().size()>0){
			transtr.setVisibility(View.VISIBLE);
		}
	
	}
	
	private View transtr ;
	
	
	
	@Override
	public void initViews() {
		dbManager = new DBManager(getActivity());
		dbManager.open();
		shareUtils=new ShareUtils(getActivity());
		transtr = getView().findViewById(R.id.jd_dist_layout_download_view);
		getView().findViewById(R.id.jd_dist_down_gong).setOnClickListener(this);
		transtr.setOnClickListener(this);
		listView = (PullToRefreshListView) getView().findViewById(R.id.xListView);
		group = (ViewGroup) getView().findViewById(R.id.jd_locationID);
		 horizontalScrollView = (HorizontalScrollView) getView()
				.findViewById(R.id.jd_hor);
		 upLoad = getView().findViewById(R.id.net_d_upload);
		download = getView().findViewById(R.id.net_d_download);
		share = getView().findViewById(R.id.net_d_share);
		more_dis = getView().findViewById(R.id.net_d_more_dis);
		menuMore = new MenuMore(this);

		menuMore.setOnDismissListener(new OnDismissListener() {
			public void onDismiss() {
				nor.setVisibility(View.VISIBLE);
				dis.setVisibility(View.GONE);
				menuMoreShow = false;
			}
		});
		upLoad.setOnClickListener(this);
		download.setOnClickListener(this);
		share.setOnClickListener(this);
		more_dis.setOnClickListener(this);
		 popMenu = new PopMenu(this,(MainFragmentActivity) getActivity(), getView());
		 nor = (TextView) getView().findViewById(R.id.net_d_more_btn_nor);
			dis = (TextView) getView().findViewById(R.id.net_d_more_btn_dis);
		 nor.setOnClickListener(this);
	}
	
	public FilesListEntity<FileInfo0> getFilesListEntity(){
		return entity;
	}
	
	
	public void refresh(){
		listView.setState(State.REFRESHING, true);
		lianwang(getFileList(/*-1, 0, entity.getCid(),sortName,ascOrDesc*/ FTYPE.NORMAL, 0), new OnLianWangFinishLisenter<FilesListEntity>() {
			public void onError(int errorCode) {
				listView.onRefreshComplete();
			}

			public void onFinish(FilesListEntity t) {
				if (t == null) {
					return;
				}
				//if(entity.getCid()==t.getCid())
				{
					//初始化
					entity = t;
					adapter.notifyDataSetChanged();
					listView.onRefreshComplete();
				}
			}
		});
	}

	@Override
	public void setLogic() {
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				/*if(isSearch){
					lianwang(getRearchParam(), new OnLianWangFinishLisenter<FileFindListEntity>() {

						@Override
						public void onFinish(FileFindListEntity t) {
							if(t==null){
								return;
							}
							entity = t.getFilesListEntity();
							adapter.notifyDataSetChanged();
						}

						@Override
						public void onError(int errorCode) {
							ToastUtils.toast(getActivity(), "网络异常");
						}
					});
					return;
				}*/
				//lianwang(getFileList(-1, 0, entity==null?0:entity.getCid(),sortName,ascOrDesc), new OnLianWangFinishLisenter<FilesListEntity>() {
				lianwang(getFileList(FTYPE.NORMAL, 0), new OnLianWangFinishLisenter<FilesListEntity>() {
					public void onError(int errorCode) {
						listView.onRefreshComplete();
					}

					public void onFinish(FilesListEntity t) {
						if (t == null) {
							return;
						}
						//初始化
						entity = t;
						adapter.notifyDataSetChanged();
						//setBar();
						listView.onRefreshComplete();
					}
				});
			}
		});
		listView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if (lastting) {

					return;
				}
				lastting = true;
				/*if(isSearch){
					if(entity.getCount()==entity.getFatherEntities().size()){
						lastting = false;
						return;
					}
					lianwang(getRearchParam(), new OnLianWangFinishLisenter<FileFindListEntity>() {

						@Override
						public void onFinish(FileFindListEntity t) {
							if(t==null){
								return;
							}
							entity.merge(t.getFilesListEntity());
							adapter.notifyDataSetChanged();
							lastting = false;
						}

						@Override
						public void onError(int errorCode) {
							ToastUtils.toast(getActivity(), "网络异常");
							lastting = false;
						}
					});
					return;
				}*/


				if (entity != null) {
					offset = entity.getCount()/*getFatherEntities().size()*/;
				}
				lianwang(getFileList(/*-1, offset, entity.getCid(),sortName,ascOrDesc*/ FTYPE.NORMAL, offset), new OnLianWangFinishLisenter<FilesListEntity>() {
					public void onError(int errorCode) {
						lastting = false;
					}

					public void onFinish(FilesListEntity t) {
						if (t == null) {
							return;
						}
						entity.merge(t);
						adapter.notifyDataSetChanged();
						lastting = false;

					}
				});
			}
		});
	}
	

	@Override
	public void setAdapter() {
		lianwang(getFileList(/*-1, 0, 0,sortName,ascOrDesc*/ FTYPE.NORMAL, 0), new OnLianWangFinishLisenter<FilesListEntity>() {
			public void onError(int errorCode) {
			}

			public void onFinish(FilesListEntity t) {
				if (t == null) {
					return;
				}
				//初始化
				entity = t;
				setBar();
				adapter.notifyDataSetChanged();
			}
		});
		adapter = new FileListViewAdapter(this);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, false));
	}
	
	
	
	
	public void onItemClick(int postion){
		//先看是文件还是文件夹
		/*FileDataFatherEntity*/ FileInfo0 e = entity./*getFatherEntities()*/getList().get(postion);
		
		/*if(e instanceof FileDirDataEntity){
			if(isSearch){
				lianwang(*//*getFileList(-1, 0, ((FileDirDataEntity) e).getCid(),sortName,ascOrDesc)*//*getFileList(FTYPE.NORMAL,0), new OnLianWangFinishLisenter<FilesListEntity>() {
					public void onError(int errorCode) {
					}
					public void onFinish(FilesListEntity t) {
						if(t==null){
							return;
						}
						//初始化
						entity = t;
						setBar();
						adapter.notifyDataSetChanged();
					}
				});
				isSearch = false;
				
				return;
			}
			addBarItem((FileDirDataEntity) e);
			entity.setCid(((FileDirDataEntity) e).getCid());
			entity.setFatherEntities(new ArrayList<FileDataFatherEntity>());
			adapter.notifyDataSetChanged();
			refresh();
		}else*/
		{
			//FileInfo0 dataEntity =  e;
			FileInfo0 dataDBEntity =  dbManager.getDownloadedFile(e.getObjid());
			if(dataDBEntity!=null){
				try{
				Intent i = FileOpenUtils.openFile(dataDBEntity.getFilePath()/*+"/"+dataDBEntity.getN()*/);
				startActivity(i);
				}catch (Exception d){
					ToastUtils.toast(getActivity(), "本机无法打开此文件");
				}
				return;
			}
			/*FileDataDBEntity*/FileInfo0 dataDBEntity2 = dbManager.getDownloadingFile(dataDBEntity.getObjid());
			if(dataDBEntity2!=null){
				ToastUtils.toast(getActivity(), "文件正在下载中");
				return;
			}
			String end = dataDBEntity.getFilename()  /* dataEntity.getN().substring(dataEntity.getN().lastIndexOf(".") + 1,
					dataEntity.getN().length()).toLowerCase()*/;
			 if (end.equalsIgnoreCase("jpg") || end.equalsIgnoreCase("gif") || end.equalsIgnoreCase("png")
						|| end.equalsIgnoreCase("jpeg") || end.equalsIgnoreCase("bmp")){
				 Intent	i= new Intent(getActivity(), PhotoShowActivity.class);
				 i.putExtra("entity", dataDBEntity);
				 startActivity(i);
			 }else{
				 ToastUtils.toast(getActivity(), "文件没有下载，点击选择下载");
			 }
			
			
			
		}
	}
	
	public void setBar(){
		Log.d("@@@", "setBar() called ");
		/*bar2 = new FileDirLocationBar(getActivity(),group,horizontalScrollView,entity.getFilePathEntities());
		bar2.setOnFilePathChange(new OnFilePathChange() {
			public void filePathChange(int cid) {
				if(cid==-1){
					return;
				}
				
				if(cid==entity.getCid())
				{
					if(isSearch){
						//文件夹
						lianwang(*//*getFileList(-1, 0, cid,sortName,ascOrDesc)*//*getFileList(FTYPE.NORMAL,0), new OnLianWangFinishLisenter<FilesListEntity>() {
							public void onError(int errorCode) {
							}
							public void onFinish(FilesListEntity t) {
								//初始化
								entity = t;
								//setBar();
								adapter.notifyDataSetChanged();
							}
						});
					}
					
				}else {
					//文件夹
					lianwang(*//*getFileList(-1, 0, cid,sortName,ascOrDesc)*//*getFileList(FTYPE.NORMAL,0), new OnLianWangFinishLisenter<FilesListEntity>() {
						public void onError(int errorCode) {
						}
						public void onFinish(FilesListEntity t) {
							//初始化
							entity = t;
							//setBar();
							adapter.notifyDataSetChanged();
						}
					});
				}
				isSearch = false;
				
			}
		});*/
	}
	
	public void addBarItem(FileDirDataEntity entity){
	/*	FilePathEntity entity2 = new FilePathEntity(entity.getN(), entity.getAid(), entity.getCid(), entity.getPid());
		bar2.addBtn(entity2);*/
		Log.d("@@@", "addBarItem called ");
	}
	
	
	//checkbox点击  
	public void checkFile(boolean isChecked,int position){
		(entity.getList().get(position)).setIsChecked( isChecked);
		if(isChecked){
			//执行选中的操作
			if(isPop==0){
				popMenu.showPopupWindows();
			}else {
				if(isPop==1){
					//下载
					popMenu.setTitle("已经选择"+getCheckedFileDataFatherEntities().size()+"个文件/*或者文件夹*/");
				}else {
					/*if(entity.getFatherEntities().get(position) instanceof FileDirDataEntity){
						//分享
						entity.getFatherEntities().get(position).isSelected = false;
						adapter.notifyDataSetChanged();
						ToastUtils.toast(getActivity(), "暂不支持文件夹分享");
						if(getCheckedFileDataFatherEntities().size()==0){
							isSelected = false;
						}
						return;
					}*/
					if(getCheckedFileDataFatherEntities().size()>1){
						entity.getList().get(position).setIsChecked( false);
						adapter.notifyDataSetChanged();
						ToastUtils.toast(getActivity(), "暂时只支持一个文件分享");
						if(getCheckedFileDataFatherEntities().size()==0){
							isChecked = false;
						}
						return;
					}
					popMenu.setTitle("已经选择"+getCheckedFileDataFatherEntities().size()+"个文件");
					
				}
			}
		}else {
			if(getCheckedFileDataFatherEntities().size()==0){
				//执行没有选中的操作
				popMenu.dis();
			}else {
				if(isPop==1){
					//下载
					popMenu.setTitle("已经选择"+getCheckedFileDataFatherEntities().size()+"个文件或者文件夹");
				}else {
					//分享
					popMenu.setTitle("已经选择"+getCheckedFileDataFatherEntities().size()+"个文件");
					
				}
			}
			
		}
	}
	
	
	
	/*public List<FileDataFatherEntity> getCheckedFileDataFatherEntities(){
		List<FileDataFatherEntity> dataEntities = new ArrayList<FileDataFatherEntity>();
		if(entity==null){
			return dataEntities;
		}
		for (int i = 0; i < entity.getFatherEntities().size(); i++) {
			if(entity.getFatherEntities().get(i).isSelected){
				dataEntities.add(entity.getFatherEntities().get(i));
			}
		}
		return dataEntities;
	}*/


	public List<FileInfo0> getCheckedFileDataFatherEntities(){
		List<FileInfo0> dataEntities = new ArrayList();
		if(entity==null){
			return dataEntities;
		}
		/*for (int i = 0; i < entity.getFatherEntities().size(); i++)*/
		for (FileInfo0 f:entity.getList())
		{
			if(f.isSelected()){
				dataEntities.add(f);
			}
		}
		return dataEntities;
	}


	public List<FileInfo0> getCheckedFiles(){
		List<FileInfo0> dataEntities = new ArrayList<FileInfo0>();
		if(entity==null){
			return dataEntities;
		}
		/*for (int i = 0; i < entity.getFatherEntities().size(); i++)*/
		for (FileInfo0 f:entity.getList())
		{
			if(/*entity.getFatherEntities().get(i).isSelected*/f.isSelected()  )
			{
				/*dataEntities.add(entity.getFatherEntities().get(i));*/
				dataEntities.add(f);
			}
		}
		return dataEntities;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.net_d_upload:
			Intent i = new Intent(getActivity(), UpLoadActivity.class);
			if(entity!=null){
				i.putExtra("entity", entity);
			}
			startActivity(i);
			break;
		case R.id.net_d_share:
			popMenu.showSharePopMenu();
			isPop = 2;
			break;
		case R.id.net_d_download:
			popMenu. showSharePopMenuDow();
			isPop = 1;
			break;
		case R.id.jd_dist_down_gong:
			transtr.setVisibility(View.GONE);
			break;

		case R.id.net_d_more_btn_nor:
			menuMore.show(more_dis);
			nor.setVisibility(View.GONE);
			dis.setVisibility(View.VISIBLE);
			menuMoreShow = true;
			break;
		case R.id.net_d_more_btn_dis:
			menuMore.dismiss();
			dis.setVisibility(View.GONE);
			nor.setVisibility(View.VISIBLE);
			break;

		case R.id.jd_dist_layout_download_view:
			Intent intent = new Intent(getActivity(), DownloadED.class);
			intent.putExtra("type", 0);
			startActivity(intent);
			break;
		default:
			break;
		}
	}


	RequestParam pr;
	
	private boolean isSearch = false;
	private String rearchName = "";
	

	
	private String sortName = FILE_NAME;
	
	private int ascOrDesc = ASC;
	
	public void setSort(String sortName,int ascOrDesc){
		this.sortName = sortName;
		this.ascOrDesc = ascOrDesc;
		refresh();
	}

	public String getSortName() {
		return sortName;
	}


	public int getAscOrDesc() {
		return ascOrDesc;
	}


	
	public void dis(){
		/*for (int i = 0; i < entity.getFatherEntities().size(); i++) {
			entity.getFatherEntities().get(i).isSelected = false;
		}*/
		for (FileInfo0 f:entity.getList())
		{
			f.setIsChecked(false);
		}

		popMenu.dis();
		adapter.notifyDataSetChanged();
	}
	
	public void checkAll(boolean checked){
		if(entity==null){
			return;
		}

		for (FileInfo0 f:entity.getList())
		{
			f.setIsChecked(checked);
		}
		if(checked==false){
			adapter.refreshChecked();
		}else {
		adapter.notifyDataSetChanged();
		}
	}
	
	public void delete(){
		lianwang(getDeleteFileParam(), new OnLianWangFinishLisenter<DeleteFileEntity>() {

			@Override
			public void onFinish(DeleteFileEntity t) {
				if(t!=null){
					if("".equals(t.getError())){
						ToastUtils.toast(getActivity(), "删除成功");
					}else {
						ToastUtils.toast(getActivity(), t.getError());
					}
					refresh();
				}else {
					ToastUtils.toast(getActivity(), "网络错误");
				}
			}

			@Override
			public void onError(int errorCode) {
				ToastUtils.toast(getActivity(), "网络错误");
			}
		});
	}
	/*public RequestParam getDeleteFileParam(){
		List<FileDataFatherEntity> entities =getCheckedFileDataFatherEntities();
		RequestParam param = new RequestParam();
		ShareUtils shareUtils = new ShareUtils(getActivity());
		param.url = shareUtils.getURL()+"/a1/index?ct=file&ac=delete";
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		StringBuffer buf = new StringBuffer();
		for(int i=0;i<entities.size();i++){
			FileDataFatherEntity entity = entities.get(i);
			if(entity instanceof FileDirDataEntity){
				if(i==entities.size()-1){
					buf.append(((FileDirDataEntity)entity).getCid());
				}else {
					buf.append(((FileDirDataEntity)entity).getCid()+",");
				}
				
			}else {
				if(i==entities.size()-1){
					buf.append(((FileDataEntity)entity).getFid());
				}else {
					buf.append(((FileDataEntity)entity).getFid()+",");
				}
			}
		}
		System.out.println(buf.toString());
		param.method = RequestParam.POST;
		pairs.add(new BasicNameValuePair("fid", buf.toString()));
		param.pairs = pairs;
		param.parse = new DeleteFileParse();
		
		return param;
		
	}*/

	public RequestParam getDeleteFileParam(){
		List<FileInfo0> entities =getCheckedFileDataFatherEntities();
		RequestParam param = new RequestParam();
//		ShareUtils shareUtils = new ShareUtils(getActivity());
//		param.url = shareUtils.getURL()+"/a1/index?ct=file&ac=delete";
//		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
//		StringBuffer buf = new StringBuffer();
//		for(int i=0;i<entities.size();i++){
//			FileDataFatherEntity entity = entities.get(i);
//			if(entity instanceof FileDirDataEntity){
//				if(i==entities.size()-1){
//					buf.append(((FileDirDataEntity)entity).getCid());
//				}else {
//					buf.append(((FileDirDataEntity)entity).getCid()+",");
//				}
//
//			}else {
//				if(i==entities.size()-1){
//					buf.append(((FileDataEntity)entity).getFid());
//				}else {
//					buf.append(((FileDataEntity)entity).getFid()+",");
//				}
//			}
//		}
//		System.out.println(buf.toString());
//		param.method = RequestParam.POST;
//		pairs.add(new BasicNameValuePair("fid", buf.toString()));
//		param.pairs = pairs;
//		param.parse = new DeleteFileParse();

		param.setMethod(2);
		param.setList(entities);


		return param;

	}

	/*
	public RequestParam getDeleteFileParam(){
		List<FileDataFatherEntity> entities =getCheckedFileDataFatherEntities();
		RequestParam param = new RequestParam();
		ShareUtils shareUtils = new ShareUtils(getActivity());
		param.url = shareUtils.getURL()+"/a1/index?ct=file&ac=delete";
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		StringBuffer buf = new StringBuffer();
		for(int i=0;i<entities.size();i++){
			FileDataFatherEntity entity = entities.get(i);
			if(entity instanceof FileDirDataEntity){
				if(i==entities.size()-1){
					buf.append(((FileDirDataEntity)entity).getCid());
				}else {
					buf.append(((FileDirDataEntity)entity).getCid()+",");
				}

			}else {
				if(i==entities.size()-1){
					buf.append(((FileDataEntity)entity).getFid());
				}else {
					buf.append(((FileDataEntity)entity).getFid()+",");
				}
			}
		}
		System.out.println(buf.toString());
		param.method = RequestParam.POST;
		pairs.add(new BasicNameValuePair("fid", buf.toString()));
		param.pairs = pairs;
		param.parse = new DeleteFileParse();

		return param;

	}
*/


	@Override
	public boolean onBack() {
		// TODO Auto-generated method stub
		if(isPop!=0||getCheckedFileDataFatherEntities().size()!=0){
			popMenu.dis();
			checkAll(false);
			return false;
		}
		
		return true;
	}
	
	public void DownBatch(){
		transtr.setVisibility(View.VISIBLE);
		for (int i = 0; i < getCheckedFileDataFatherEntities().size(); i++) {
			//if(getCheckedFileDataFatherEntities().get(i)/* instanceof FileDataEntity*/)
			{
				down( getCheckedFileDataFatherEntities().get(i));
			}/*else {
				down((FileDirDataEntity)getCheckedFileDataFatherEntities().get(i), 0);
			}*/
		}
	}

	@Override
	public void pageChange() {
		popMenu.dis();
		checkAll(false);
	}
	
}
