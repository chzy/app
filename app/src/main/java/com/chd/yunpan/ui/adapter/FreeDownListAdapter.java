package com.chd.yunpan.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.app.backend.AppInfo0;
import com.chd.base.backend.SyncTask;
import com.chd.proto.FTYPE;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

public class FreeDownListAdapter extends BaseAdapter {
    private Context mContext;
    private List<AppInfo0> _list;

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;
    private String appPath;
    private SyncTask mSyncTask;
    public  FreeDownListAdapter(Context context, List<AppInfo0> list)
    {
        this.mContext=context;
        this._list=list;
        this.mSyncTask=new SyncTask(context, FTYPE.STORE);
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.pic_test1)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
       appPath= new ShareUtils(context).getApkFile().getPath()+"/";


    }

    @Override
    public int getCount() {
        return _list==null ?  0: _list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MenuItem item = null;
        if (convertView == null) {
            item = new MenuItem();
            convertView = View.inflate(mContext, R.layout.freedown_listitem, null);
            item.text_index = (TextView) convertView.findViewById(R.id.freedown_list_item_index);
            item.text_appname = (TextView) convertView.findViewById(R.id.freedown_list_item_appname);
            item.text_appintro = (TextView) convertView.findViewById(R.id.freedown_list_item_appintro);
            item.btn_get = (Button) convertView.findViewById(R.id.freedown_list_item_btn);
            item.img_url = (ImageView) convertView.findViewById(R.id.freedown_list_item_img);
            convertView.setTag(item);
		} else {
			item = (MenuItem) convertView.getTag();
		}

        if (_list.get(position)!=null)
        {

            AppInfo0 appInfo0 = _list.get(position);
            item.text_index.setText(String.format("%d", position + 1));
            item.text_appname.setText(appInfo0.getAppName());
            item.text_appintro.setText(appInfo0.getAppVersion());
            //下载地址
            String url=appInfo0.getUrl();
//            item.img_url.setImageDrawable(_list.get(position).getIco_url());
            String icon=appInfo0.getIco_url();

            imageLoader.displayImage(icon,item.img_url,options);
            item.btn_get.setTag(position);
            item.btn_get.setOnClickListener(new View.OnClickListener() 
            {
				
				@Override
				public void onClick(View v) 
				{
				    int pos= (int) v.getTag();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }).start();

				}
				
			});
        }

        return convertView;
    }


    class MenuItem
    {
        TextView text_index, text_appname, text_appintro;
        Button btn_get;
        ImageView img_url;
    }


}
