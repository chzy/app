package com.chd.yunpan.ui.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.chd.Entity.FilesListEntity;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.ui.adapter.MusicListGrid_Adapter;
import com.chd.yunpan.ui.fragment.popupwindow.CameraPopMenu;
import com.chd.yunpan.ui.fragment.popupwindow.CameraPopMenuDelete;
import com.chd.yunpan.ui.fragment.popupwindow.CameraPopMenuDown;
import com.chd.yunpan.ui.fragment.popupwindow.CameraPopMenuShare;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

//import com.chd.yunpan.parse.entity.FileDataEntity;
//import com.chd.yunpan.parse.entity.FilesListEntity;

public class MusicListFragment extends BaseFragment  implements OnClickListener{

	private FilesListEntity<FileInfo0> entity;

	private View noEmptyView;

	private View enptyView;
	
	private CameraPopMenu menu = null;
	private LinearLayout send, download, delete;
	private CameraPopMenuDown menuDown = null;
	private CameraPopMenuDelete menuDelete = null;
	private CameraPopMenuShare menuShare = null;

	private PullToRefreshListView listView;

	private MusicListGrid_Adapter adapter;

	// 最后一个后，是否在加载
	private boolean lastting = false;

	private int offset = 0;

	private boolean isPop = false;

	@Override
	public View createView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		//downFileFork();
		return inflater.inflate(R.layout.activity_music, null);
		/*menuShare = new CameraPopMenuShare(getActivity(),
				getActivity(), this);*/

		//menu = new CameraPopMenu(getActivity(), getActivity(), this);
	}

	/**
	 *
	 */
	@Override
	public void initViews() {

		//noEmptyView = getView().findViewById(R.id.camera_main_noempty);
		//enptyView = getView().findViewById(R.id.camera_main_empty);
		/*listView = (PullToRefreshListView) getView().findViewById(
				R.id.xListView0);
*/

		//download.setOnClickListener(this);
		//delete.setOnClickListener(this);
		//send.setOnClickListener(this);

		//menuDown = new CameraPopMenuDown(getActivity(), this);
		//menuDelete = new CameraPopMenuDelete(getActivity(), this);
		//menuShare = new CameraPopMenuShare(getActivity(),
		//		getActivity(), this);

	}


	/*public List<FileInfo0> getEntity() {
		List<FileInfo0> entities = new ArrayList<*//*FileDataEntity*//*FileInfo0>();
		if(entity!=null)

		for(FileInfo0 f : entity.getList()  )
		{
			entities.add(f );
		}

		return entities;
	}
*/
	@Override
	public void setLogic() {
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				lianwang( getFileList(FTYPE.MUSIC,0),
						new OnLianWangFinishLisenter<FilesListEntity>() {
							public void onError(int errorCode) {
								listView.onRefreshComplete();
							}

							public void onFinish(FilesListEntity t) {
								// 初始化
								entity = t;
								//list = getTileAndFilesListEntity();
								adapter.notifyDataSetChanged();
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
				if (entity != null) {
					offset = entity.getList().size();
				}
				lianwang(getFileList(FTYPE.PICTURE,offset),
						new OnLianWangFinishLisenter<FilesListEntity>() {
							public void onError(int errorCode) {
								lastting = false;
							}

							public void onFinish(FilesListEntity t) {
								entity.merge(t);
								//list = getTileAndFilesListEntity();
								adapter.notifyDataSetChanged();
								lastting = false;
								//showBackGroud();
							}
						});
			}
		});
	}

	/*public void showBackGroud(){
		if(entity==null|| entity.getList()==null||entity.getList().isEmpty() ){
			enptyView.setVisibility(View.VISIBLE);
			noEmptyView.setVisibility(View.GONE);
		}else {
			enptyView.setVisibility(View.GONE);
			noEmptyView.setVisibility(View.VISIBLE);
		}
	}*/

	@Override
	public void setAdapter() {

		adapter = new MusicListGrid_Adapter(this);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

		lianwang(getFileList(FTYPE.PICTURE, 0),
				new OnLianWangFinishLisenter<FilesListEntity>() {
					public void onError(int errorCode) {
					}

					public void onFinish(FilesListEntity t) {
						// 初始化
						entity = t;
						list = getTileAndFilesListEntity();
						adapter.notifyDataSetChanged();
						//showBackGroud();

					}
				});
	}

	public FilesListEntity getFilesListEntity() {
		return entity;
	}

	private List list = new ArrayList();
	public List getList() {
		return list;
	}

	@Override
	public void onResume() {


		super.onResume();
	}

	public List getTileAndFilesListEntity() {
		List list = new ArrayList();

			return list;
		}

	/*public List getTileAndFilesListEntity() {
		List list = new ArrayList();
		String lastTime = null;
		if(getFilesListEntity()==null){
			return list;
		}
		List<FileInfo0> entities = new ArrayList<FileInfo0>();
		int size = 0,i=0;
		List<FileInfo0> lss=getFilesListEntity().getList();
		int lstlen=lss.size();
		for( FileInfo0 f : lss )
		{
			if (i == 0) {
				lastTime = f.getLastModified();
				list.add(lastTime);
				entities.add(f);
				size=1;
			} else {
				if (f.getLastModified().equals(lastTime))
				{
					entities.add(f);
					size++;
					if(size==3||i==lstlen-1){
						list.add(entities);
						*//*entities = new ArrayList<FileDataEntity>();*//*
						entities.clear();
						size=0;
					}
				} else {
					if(size>0){
						list.add(entities);
						*//*entities = new ArrayList<FileDataEntity>();*//*
						entities.clear();
					}
					lastTime = *//*DateUtils.parse2Date(entity.getT())*//*f.getLastModified();
					list.add(lastTime);
					entities.add(*//*entity*//*f);
					size=1;
				}
			}

		}
		return list;

	}*/

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//ToastUtils.toast(getActivity(), requestCode+"-----"+resultCode);
		if(requestCode==-1){
			if(resultCode == 1) {
				refreshWithDataChange();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onBack() {
		if (MusicListFragment.this.menuDown.havePop()) {
			MusicListFragment.this.menuDown.dis();
			return false;
		}
		if (MusicListFragment.this.menuDelete.havePop()) {
			MusicListFragment.this.menuDelete.dis();
			return false;

		}
		if (MusicListFragment.this.menuShare.havePop()) {
			MusicListFragment.this.menuShare.dis();
			return false;
		}
		if(menu.havePop()){
			menu.dis();
			return false;
		}
		return true;
	}

	public void refresh(){
		adapter.notifyDataSetChanged();
	}


	public void refreshWithDataChange(){
		listView.setState(State.REFRESHING, true);

		//FTYPE.
		lianwang(getFileList(FTYPE.PICTURE, 0),
				new OnLianWangFinishLisenter<FilesListEntity>() {
					public void onError(int errorCode) {
					}

					public void onFinish(FilesListEntity t) {
						// 初始化
						entity = t;
						list = getTileAndFilesListEntity();
						adapter.notifyDataSetChanged();
						//showBackGroud();
					}
				});
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case /*R.id.camera_main_bn_down*/2:
			if (!MusicListFragment.this.menuDown.havePop()) {
				MusicListFragment.this.menuDown.showPopupWindows(v);
				adapter.setChecked(true);
				adapter.notifyDataSetChanged();
			}
			break;
		case /*R.id.camera_main_bn_del*/1:
			if (!MusicListFragment.this.menuDelete.havePop()) {
				MusicListFragment.this.menuDelete.showPopupWindows(v);
				adapter.setChecked(true);
				adapter.notifyDataSetChanged();
			}
			break;
		case /*R.id.camera_main_bn_send*/0:
			if (!MusicListFragment.this.menuShare.havePop()) {
				MusicListFragment.this.menuShare.showPopupWindows(v);
				adapter.setChecked(true);
				adapter.notifyDataSetChanged();
			}
			break;
		default:
			break;
		}
	}

	public void dis(){
		isChecked = false;
		if(entity==null){
			return;
		}
		adapter.setChecked(false);
		setCheckedAll(false);
		adapter.refreshChecked();
	}
	public void setCheckedAll(boolean flag){
		if(entity!=null)
			/*for (int i = 0; i < entity.getFatherEntities().size(); i++) {
				FileDataEntity entity1 = (FileDataEntity) entity.getFatherEntities().get(i);
				entity1.isSelected = flag;
		}*/
		for (FileInfo0 f: entity.getList())
		{
			f.setIsChecked(flag);
		}
	}

	@Override
	public void pageChange() {
		int count = 0;
		if (MusicListFragment.this.menuDown.havePop()) {
			MusicListFragment.this.menuDown.dis();
			count ++;
		}
		if (MusicListFragment.this.menuDelete.havePop()) {
			MusicListFragment.this.menuDelete.dis();
			count++;
			
		}
		if (MusicListFragment.this.menuShare.havePop()) {
			MusicListFragment.this.menuShare.dis();
			count++;
		}
		if(menu.havePop()){
			menu.dis();
			count++;
		}
		if(count==0){
			dis();
		}
	}
	
	public void showMenu(){
		if(!menu.havePop()){
			menu.showPopupWindows(download);
		}
	}
	
	public void check(FileInfo0 entity,boolean isChecked){
		entity.setIsChecked(isChecked);
		if (MusicListFragment.this.menuDown.havePop()) {
			menuDown.check();
			return;
		}
		if (MusicListFragment.this.menuDelete.havePop()) {
			menuDelete.check();
			return;
			
		}
		if (MusicListFragment.this.menuShare.havePop()) {
			menuShare.check();
			return;
		}
		if(getCheckedFileDataEntity().size()==0){
			this.isChecked = false;
			dis();
			if(menu.havePop()){
				menu.dis();
			}
		}
		
	}

	
	public List<FileInfo0> getCheckedFileDataEntity(){
		List<FileInfo0> dataEntities = new ArrayList<FileInfo0>();
		if(entity!=null) {
			//int totals = entity.getList().size();
			/*for (int i = 0; i < entity.getFatherEntities().size(); i++)*/
			for (FileInfo0 f : entity.getList())
			{
				/*FileDataEntity entity1 = (FileDataEntity) entity.getFatherEntities().get(i);
				if (entity1.isSelected) {
					dataEntities.add(entity1);
				}*/
				if (f.isSelected())
				{
					dataEntities.add(f);
				}
			}
		}
		return dataEntities;
	}
	public boolean isChecked = false;

}
