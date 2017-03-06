package com.chd.yunpan.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.app.backend.AppInfo0;
import com.chd.base.backend.DownFileUtils;
import com.chd.yunpan.R;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.utils.AutoInstall;
import com.chd.yunpan.view.circleimage.CircularProgressButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

public class FreeDownListAdapter extends BaseAdapter {
    private Activity mContext;
    private List<AppInfo0> _list;

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;
    private String path;
    public  FreeDownListAdapter(Activity context, List<AppInfo0> list)
    {
        this.mContext=context;
        this._list=list;
        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.pic_test1)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
        this.path=new ShareUtils(mContext).getApkFile().getPath();


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
            item.btn_get = (CircularProgressButton) convertView.findViewById(R.id.freedown_list_item_btn);
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

//            item.img_url.setImageDrawable(_list.get(position).getIco_url());
            String icon=appInfo0.getIco_url();

            imageLoader.displayImage(icon,item.img_url,options);
            item.btn_get.setTag(position);
            item.btn_get.setOnClickListener(new View.OnClickListener() 
            {
				
				@Override
				public void onClick(final View v)
				{

                    //下载函数
                    CircularProgressButton btn= (CircularProgressButton) v;
                    int pos= (Integer) v.getTag();
                    final String url=_list.get(pos).getUrl();
                    String filename = url.substring(url.lastIndexOf("/") + 1);
                    //下载地址


                    if(btn.getProgress()==0||btn.getProgress()==-1){


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            new DownFileUtils(mContext, (CircularProgressButton) v,url);
                        }
                    }).start();
                    }else if(btn.getProgress()==100){
                        AutoInstall.setUrl(path+"/"+filename);
                        AutoInstall.install(mContext);
                    }else{

                    }

				}
				
			});
        }

        return convertView;
    }


    class MenuItem
    {
        TextView text_index, text_appname, text_appintro;
        CircularProgressButton btn_get;
        ImageView img_url;
    }


}
