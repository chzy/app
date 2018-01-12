package com.chd.photo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.chd.photo.ui.PicDetailActivity;
import com.chd.proto.FileInfo;
import com.chd.video.VideoPlayActivity;
import com.chd.yunpan.R;
import com.chd.yunpan.utils.TimeUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class PicAdapter<E extends FileInfo> extends BaseQuickAdapter<List<E>, BaseViewHolder> {

    private Activity context;
    private boolean bIsUbkList;
    private ImageLoader imageLoader;

    private boolean isVideo;
    private boolean isCheck = true;

    public PicAdapter(Activity picActivity, List<List<E>> localList, ImageLoader imageLoader, boolean isVideo, boolean showSelect) {
        super(R.layout.item_pic_adapter, localList);
        this.context = picActivity;
        this.isVideo = isVideo;
        this.imageLoader = imageLoader;
        this.showSelect = showSelect;
    }

    public boolean isbIsUbkList() {
        return bIsUbkList;
    }

    public void setbIsUbkList(boolean bIsUbkList) {
        this.bIsUbkList = bIsUbkList;
    }

    private boolean showSelect;

    public void setShowSelect(boolean showSelect) {
        this.showSelect = showSelect;
        notifyDataSetChanged();
    }

    public boolean isShowSelect() {
        return showSelect;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final List<E> item) {
        String start = TimeUtils.getDay(item.get(0).getLastModified());
        String end = TimeUtils.getDay(item.get(item.size() - 1).getLastModified());
        if (start.equals(end)) {
            helper.setText(R.id.tv_pic_date, start);
        } else {
            helper.setText(R.id.tv_pic_date, end + "至" + start);
        }
        RecyclerView recyclerView = helper.getView(R.id.mlv_pic);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4, LinearLayoutManager.VERTICAL, false));
        final PicInfoAdapter<E> infoAdapter = new PicInfoAdapter<E>(item
                , imageLoader, showSelect, isVideo, isCheck);
        infoAdapter.setPosition(helper.getAdapterPosition());
        recyclerView.setAdapter(infoAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnItemTouchListener(onItemClickListener);
    }


    private SimpleClickListener onItemClickListener = new SimpleClickListener() {

        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            PicInfoAdapter<E> infoAdapter = ((PicInfoAdapter) adapter);
            if (!isVideo) {
                //非视频，即图片进去
                Intent intent = new Intent(context, PicDetailActivity.class);
                intent.putExtra("bean", infoAdapter.getItem(position));
                intent.putExtra("pos1", infoAdapter.getGroupPos());
                intent.putExtra("pos2", position);
                intent.putExtra("ubklist", bIsUbkList);
                context.startActivityForResult(intent, 0x12);
            } else {
                //视频进去
                Intent intent = new Intent(context, VideoPlayActivity.class);
                intent.putExtra("bean", infoAdapter.getItem(position));
                context.startActivityForResult(intent, 0x13);
            }
        }

        @Override
        public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {

        }

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            if (view.getId() == R.id.iv_pic_edit_item_photo_check) {
                PicInfoAdapter<E> infoAdapter = ((PicInfoAdapter) adapter);
                if (showSelect) {
                    //执行了几次
                    String s = infoAdapter.getGroupPos() + " " + position;
                    boolean contains = selectList.contains(s);
                    if (contains) {
                        selectList.remove(s);
                    } else {
                        selectList.add(s);
                    }
                    infoAdapter.changeItem(position, contains);
                    EventBus.getDefault().post(selectList.size());
                }
            }
        }

        @Override
        public void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {

        }
    };
    private ArrayList<String> selectList = new ArrayList<>();

    public ArrayList<String> getSelectData() {
        return this.selectList;
    }

    public void setSelectList(ArrayList<String> selectList) {
        this.selectList = selectList;
        this.isCheck = selectList.isEmpty();
        notifyDataSetChanged();
    }

    public void remove(int pos1, int pos2) {
        getItem(pos1).remove(pos2);
    }

    public E getFileInfo(int pos1, int pos2) {
        return getItem(pos1).get(pos2);
    }


}
