package com.chd.yunpan.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.chd.yunpan.GlideApp;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SuperRefreshRecyclerView extends RecyclerView {

    private boolean move;
    private int mIndex;
    private RecyclerView.LayoutManager layoutManager;

    private Context mContext;

    public SuperRefreshRecyclerView(Context context) {
        super(context);
        addOnScrollListener(new RecyclerViewListener());

    }

    public SuperRefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        addOnScrollListener(new RecyclerViewListener());
    }

    public SuperRefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addOnScrollListener(new RecyclerViewListener());
    }


    public void moveToPosition(int n) {
        mIndex = n;
        LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager) layoutManager;
        int firstItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLinearLayoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (n <= firstItem) {
            //当要置顶的项在当前显示的第一个项的前面时
            scrollToPosition(n);
        } else if (n <= lastItem) {
            //当要置顶的项已经在屏幕上显示时
            int top = getChildAt(n - firstItem).getTop();
            scrollBy(0, top);
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            scrollToPosition(n);
            //这里这个变量是用在RecyclerView滚动监听里面的
            move = true;
        }

    }

    ChangeScrollStateCallback mChangeScrollStateCallback;

    class RecyclerViewListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (mChangeScrollStateCallback != null)
                mChangeScrollStateCallback.change(newState);
            switch (newState) {
                case SCROLL_STATE_IDLE: // The RecyclerView is not currently scrolling.
                    //当屏幕停止滚动，加载图片
                    try {
                        if (getContext() != null) {
                            ImageLoader.getInstance().resume();
                            GlideApp.with(getContext()).resumeRequests();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case SCROLL_STATE_DRAGGING: // The RecyclerView is currently being dragged by outside input such as user touch input.
                    //当屏幕滚动且用户使用的触碰或手指还在屏幕上，停止加载图片
                    try {
                        if (getContext() != null) {
                            ImageLoader.getInstance().pause();
                            GlideApp.with(getContext()).pauseRequests();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case SCROLL_STATE_SETTLING: // The RecyclerView is currently animating to a final position while not under outside control.
                    //由于用户的操作，屏幕产生惯性滑动，停止加载图片
                    try {
                        if (getContext() != null) {
                            ImageLoader.getInstance().pause();
                            GlideApp.with(getContext()).pauseRequests();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }


        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //在这里进行第二次滚动（最后的100米！）
            if (move) {
                move = false;
                //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager) layoutManager;
                int n = mIndex - mLinearLayoutManager.findFirstVisibleItemPosition();
                if (0 <= n && n < recyclerView.getChildCount()) {
                    //获取要置顶的项顶部离RecyclerView顶部的距离
                    int top = recyclerView.getChildAt(n).getTop();
                    //最后的移动
                    recyclerView.scrollBy(0, top);
                }
            }
        }
    }

    public void setChangeScrollStateCallback(ChangeScrollStateCallback mChangeScrollStateCallback) {
        this.mChangeScrollStateCallback = mChangeScrollStateCallback;

    }

    public interface ChangeScrollStateCallback {

        public void change(int c);

    }

}