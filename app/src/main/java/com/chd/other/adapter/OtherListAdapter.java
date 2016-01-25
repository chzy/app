package com.chd.other.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class OtherListAdapter extends BaseAdapter {
    private Context mContext;
    private List<FileInfo0> _list;
    private boolean isShow;
    private ArrayList<FileInfo0> checkList;
    private Resources mResources;

    public OtherListAdapter(Context context, List<FileInfo0> list) {
        mContext = context;
        _list = list;
        checkList=new ArrayList();
        mResources=mContext.getResources();
    }

    @Override
    public int getCount() {
        return _list == null ? 0 : _list.size();
    }

    public ArrayList<FileInfo0> getCheckList() {
        return checkList;
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
            convertView = View.inflate(mContext, R.layout.other_listitem, null);
            item.text_appname = (TextView) convertView.findViewById(R.id.other_list_item_appname);
            item.text_appintro = (TextView) convertView.findViewById(R.id.other_list_item_appintro);
            item.img_url = (ImageView) convertView.findViewById(R.id.other_list_item_img);
            item.cb = (CheckBox) convertView.findViewById(R.id.cb_edit_check);
            item.text_appsize = (TextView) convertView.findViewById(R.id.other_list_item_appsize);
            convertView.setTag(item);
        } else {
            item = (MenuItem) convertView.getTag();
        }

        if (_list.get(position) != null) {


            String name=_list.get(position).getObjid();
            int last=name.lastIndexOf(".")+1;
            String substring = name.substring(last);
            int id=getResId("ft_"+substring, "drawable");

            if(id==0){
                id=getResId("ft_unknow","drawable");
            }

            item.text_appname.setText(name);
            String time= TimeUtils.getTime(_list.get(position).getLastModified());
            item.text_appintro.setText(time);
            item.img_url.setImageResource(id);
           /* item.text_appintro.setText(_list.get(position).());
            item.img_url.setImageResource(_list.get(position).getPicid());
            item.text_appsize.setText(_list.get(position).getFilesize());*/
        }
        item.cb.setTag(position);
        item.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int pos= (Integer) compoundButton.getTag();
                FileInfo0 checkItem = _list.get(pos);
                checkItem.setIsChecked(b);
                if(b){
                    //选中
                    checkList.add(checkItem);
                }else{
                    //未选中
                    checkList.remove(checkItem);
                }
            }
        });


        if(isShow){
            item.cb.setVisibility(View.VISIBLE);
        }else{
            item.cb.setVisibility(View.GONE);
        }


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


    class MenuItem {
        TextView text_appname, text_appintro, text_appsize;
        ImageView img_url;
        CheckBox cb;
    }


}
