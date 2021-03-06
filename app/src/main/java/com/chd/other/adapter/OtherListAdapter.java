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
    private List<Integer> _list;
    private boolean isShow;
    private boolean ShowUnbak = false;
    private String Showftype = null;
    private ArrayList<FileInfo> checkList;
    private Resources mResources;
    private FilelistEntity filelistEntity;

    public OtherListAdapter(Context context, List list) {
        mContext = context;
        _list = list;
        checkList = new ArrayList();
        mResources = mContext.getResources();
        filelistEntity = UILApplication.getFilelistEntity();
    }

    @Override
    public int getCount() {
        return _list == null ? 0 : _list.size();

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
        if (convertView == null)
        {
           item = new MenuItem();
            convertView = View.inflate(mContext, R.layout.other_listitem, null);
            item.text_appname = (TextView) convertView.findViewById(R.id.other_list_item_appname);
            item.text_appintro = (TextView) convertView.findViewById(R.id.other_list_item_appintro);
            item.img_url = (ImageView) convertView.findViewById(R.id.other_list_item_img);
            item.cb = (CheckBox) convertView.findViewById(R.id.cb_edit_check);
            convertView.setTag(item);
        }
        else {
            item = (MenuItem) convertView.getTag();
            item.img_url.clearAnimation();
        }
        convertView.setVisibility(View.GONE);

        if(item!=null && item.cb!=null )
        {
            item.cb.setVisibility(View.GONE);
            item.text_appintro.setVisibility(View.GONE);
            item.text_appname.setVisibility(View.GONE);
            item.img_url.setVisibility(View.GONE);
        }
        // - --------
        //设置item的weidth和height都为0
    /*    AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                 0,
                0);
        //将设置好的布局属性应用到GridView的Item上
        convertView.setLayoutParams(param);*/

        // - --------
        FileInfo fileItem;
        int idx = _list.get(position);
        if (ShowUnbak) {
            fileItem = filelistEntity.getLocalFileByIdx(idx);
        }else
            fileItem=filelistEntity.getBklist().get(idx);


        if (this.Showftype != null && !fileItem.getObjid().contains(Showftype))
            return convertView;//隐藏非特定类型的文件
        String name = fileItem.getObjid();
        int last = name.lastIndexOf(".") + 1;
        String substring = name.substring(last);
        int id = getResId("ft_" + substring, "drawable");

        if (id == 0) {
            id = getResId("ft_unknow", "drawable");
        }
        if (item!=null) {
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
                    FileInfo checkItem;
                    if (ShowUnbak) {
                       int idx = _list.get(pos);
                        checkItem=filelistEntity.getLocalFileByIdx(idx);
                    }
                    else
                    {
                        checkItem=filelistEntity.getBklist().get(pos);
                    }
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

            if(item!=null && item.cb!=null )
            {
                //item.cb.setVisibility(View.VISIBLE);
                item.text_appintro.setVisibility(View.VISIBLE);
                item.text_appname.setVisibility(View.VISIBLE);
                item.img_url.setVisibility(View.VISIBLE);
            }
            if (isShow) {
                item.cb.setVisibility(View.VISIBLE);
            } else {
                item.cb.setVisibility(View.GONE);
            }
        }
        // - --------
        /*param = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //将设置好的布局属性应用到GridView的Item上
        convertView.setLayoutParams(param);*/
// - --------
        convertView.setVisibility(View.VISIBLE);

        return convertView;
    }

    /**
     * 14
     * 根据资源的名字获取它的ID
     * 15
     *
     * @param name    16
     *                要获取的资源的名字
     *                17
     * @param defType 18
     *                资源的类型，如drawable, string 。。。
     *                19
     * @return 资源的id
     * 20
     */
    public int getResId(String name, String defType) {
        String packageName = mContext.getApplicationInfo().packageName;
        return mResources.getIdentifier(name, defType, packageName);
    }


    public void showCB(boolean isShow) {
        this.isShow = isShow;
    }

    private void setShowUnbakedfile(boolean show) {
        ShowUnbak = show;
    }

    public void setList(List lst,boolean b) {
        _list = lst;
        setShowUnbakedfile(b);
        // 加不加 不确定  执行未看出变化
        //notifyDataSetInvalidated();
    }
    public List getList(){
        return _list;
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
