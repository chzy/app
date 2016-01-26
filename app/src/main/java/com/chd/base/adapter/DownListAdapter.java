package com.chd.base.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;

import java.util.List;

/**
 * @description
 * @FileName: com.chd.base.adapter.DownListAdapter
 * @author: liumj
 * @date:2016-01-20 20:22
 * OS:Mac 10.10
 * Developer Kits:AndroidStudio 1.5
 */

public class DownListAdapter extends BaseAdapter{

    private List<FileInfo0> mDownList;
    private Context mContext;
    private LayoutInflater mInflater;
    private Resources mResources;

    public DownListAdapter(List<FileInfo0> downList,Context context){
        this.mDownList=downList;
        this.mContext=context;
        this.mInflater=LayoutInflater.from(mContext);
        this.mResources=context.getResources();
    }


    @Override
    public int getCount() {
        return mDownList==null?0:mDownList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDownList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHodler vh=null;
        Context context=viewGroup.getContext();
        if(view==null){
            view= mInflater.inflate(R.layout.item_download, viewGroup, false);
            vh=new ViewHodler(view);
            view.setTag(vh);
        }else{
            vh= (ViewHodler) view.getTag();
        }

        FileInfo0 info0=mDownList.get(position);
        vh.item_title.setText(info0.getObjid());

        String name=info0.getObjid();
        int last=name.lastIndexOf(".")+1;
        String substring = name.substring(last);
        int id=getResId("ft_"+substring, "drawable");

        if(id==0){
            id=getResId("ft_unknow","drawable");
        }
        vh.item_icon.setImageResource(id);
        return view;
    }

   public class ViewHodler{
        TextView item_title;
        TextView item_status;
        ImageView item_icon;
        SeekBar  item_seekbar;
       public ViewHodler(View itemView){
           item_title= (TextView) itemView.findViewById(R.id.item_download_title);
           item_status= (TextView) itemView.findViewById(R.id.item_download_status);
           item_icon= (ImageView) itemView.findViewById(R.id.item_download_icon);
           item_seekbar= (SeekBar) itemView.findViewById(R.id.item_download_seekbar);
       }
    }

    /**
     14
     * 根据资源的名字获取它的ID
     15
     * @param name
    16
     *            要获取的资源的名字
    17
     * @param defType
    18
     *            资源的类型，如drawable, string 。。。
    19
     * @return 资源的id
    20
     */
    public int getResId(String name, String defType) {
        String packageName = mContext.getApplicationInfo().packageName;
        return mResources.getIdentifier(name, defType, packageName);
    }

}
