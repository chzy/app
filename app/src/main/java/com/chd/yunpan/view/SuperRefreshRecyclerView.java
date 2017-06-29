package com.chd.yunpan.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class SuperRefreshRecyclerView extends RecyclerView {

    private boolean move;
    private int mIndex;
    private RecyclerView.LayoutManager layoutManager;

    private Context mContext;

    public SuperRefreshRecyclerView(Context context) {
        super(context);
    }

    public SuperRefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperRefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void moveToPosition(int n) {
        mIndex=n;
        LinearLayoutManager  mLinearLayoutManager=(LinearLayoutManager)layoutManager;
        int firstItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLinearLayoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (n <= firstItem ){
            //当要置顶的项在当前显示的第一个项的前面时
            scrollToPosition(n);
        }else if ( n <= lastItem ){
            //当要置顶的项已经在屏幕上显示时
            int top = getChildAt(n - firstItem).getTop();
           scrollBy(0, top);
        }else{
            //当要置顶的项在当前显示的最后一项的后面时
            scrollToPosition(n);
            //这里这个变量是用在RecyclerView滚动监听里面的
            move = true;
        }

    }

    ChangeScrollStateCallback mChangeScrollStateCallback;

    class RecyclerViewListener extends RecyclerView.OnScrollListener{

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            mChangeScrollStateCallback.change(newState);

            //Log.d("AAAAAAAAAAAAAAA", "" + newState);


        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //在这里进行第二次滚动（最后的100米！）
            if (move ){
                move = false;
                //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                LinearLayoutManager mLinearLayoutManager=(LinearLayoutManager)layoutManager;
                int n = mIndex - mLinearLayoutManager.findFirstVisibleItemPosition();
                if ( 0 <= n && n < recyclerView.getChildCount()){
                    //获取要置顶的项顶部离RecyclerView顶部的距离
                    int top = recyclerView.getChildAt(n).getTop();
                    //最后的移动
                    recyclerView.scrollBy(0, top);
                }
            }
        }
    }

    public void setChangeScrollStateCallback(ChangeScrollStateCallback mChangeScrollStateCallback){
        this.mChangeScrollStateCallback=mChangeScrollStateCallback;

    }

    public interface ChangeScrollStateCallback  {

        public void change(int c);

    }

}