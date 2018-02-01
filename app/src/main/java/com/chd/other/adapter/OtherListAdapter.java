package com.chd.other.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.base.Entity.FilelistEntity;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class OtherListAdapter extends BaseAdapter {
    private Context mContext;
    private List<FileInfo> _list;
    private List<Integer> _unbak_idx_lst;
    private boolean isShow;
    private boolean ShowUnbak =false;
    private String Showftype=null;
    private ArrayList<FileInfo> checkList;
    private Resources mResources;
    private FilelistEntity filelistEntity;

    public OtherListAdapter(Context context, List<FileInfo> list) {
        mContext = context;
        _list = list;
        checkList=new ArrayList();
        mResources=mContext.getResources();
        filelistEntity= UILApplication.getFilelistEntity();
    }

    @Override
    public int getCount() {

            if (ShowUnbak)
               return filelistEntity.getUnbak_idx_lst().size();
            else
                return _list==null?0:_list.size();

    }

    public ArrayList<FileInfo> getCheckList() {
        return checkList;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        MenuItem item = null;
        if (convertView == null) {
            item = new MenuItem();
            convertView = View.inflate(mContext, R.layout.other_listitem, null);
            item.text_appname = (TextView) convertView.findViewById(R.id.other_list_item_appname);
            item.text_appintro = (TextView) convertView.findViewById(R.id.other_list_item_appintro);
            item.img_url = (ImageView) convertView.findViewById(R.id.other_list_item_img);
            item.cb = (CheckBox) convertView.findViewById(R.id.cb_edit_check);
            convertView.setTag(item);
        } else {
            item = (MenuItem) convertView.getTag();
        }
       // convertView.setVisibility(View.INVISIBLE);
        //convertView.setEnabled(false);
        convertView.setVisibility(View.GONE);
        item.cb.setVisibility(View.GONE);
        FileInfo fileItem =  _list.get(position);
        if (fileItem==null)
            return convertView;;
        if (ShowUnbak) {
            FileInfo0 it = (FileInfo0) fileItem;
            if (fileItem != null) {
                if (it.backuped ) {
                    return convertView;
                }
            }
        }

            if (this.Showftype != null && !fileItem.getObjid().contains(Showftype))
                return convertView;;
            String name = fileItem.getObjid();
            int last = name.lastIndexOf(".") + 1;
            String substring = name.substring(last);
            int id = getResId("ft_" + substring, "drawable");

            if (id == 0) {
                id = getResId("ft_unknow", "drawable");
            }

            item.text_appname.setText(name);
            String time = TimeUtils.getTime(fileItem.getLastModified());
            item.text_appintro.setText(time);
            item.img_url.setImageResource(id);

            item.cb.setTag(position);
            item.cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean check = ((CheckBox) view).isChecked();
                    int pos = (Integer) view.getTag();
                    FileInfo checkItem = _list.get(pos);
//                checkItem.set(check);
                    if (check) {
                        //选中
                        checkList.add(checkItem);
                    } else {
                        //未选中
                        checkList.remove(checkItem);
                    }
                    notifyDataSetChanged();
                }
            });
            if (isShow) {
                item.cb.setVisibility(View.VISIBLE);
            } else {
                item.cb.setVisibility(View.GONE);
            }


        convertView.setVisibility(View.VISIBLE);
        return convertView;
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



    public void showCB(boolean isShow){
        this.isShow=isShow;
    }

    public void setShowUnbakedfile(boolean show ) {
        ShowUnbak = show ;
    }

    public void setList(List lst) {
        _list=lst;
    }

    public void setShowfiletype(String type) {
        this.Showftype = type;
    }




    class MenuItem {
        TextView text_appname, text_appintro, text_appsize;
        ImageView img_url;
        CheckBox cb;
    }


}
