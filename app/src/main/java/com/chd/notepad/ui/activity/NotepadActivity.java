package com.chd.notepad.ui.activity;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chd.notepad.service.SyncService;
import com.chd.notepad.ui.adapter.ListViewAdapter;
import com.chd.notepad.ui.db.DatabaseManage;
import com.chd.notepad.ui.item.NoteItemtag;
import com.chd.yunpan.R;

import java.util.ArrayList;
import java.util.Calendar;


public class NotepadActivity extends ListActivity implements OnScrollListener {
    /**
     * Called when the activity is first created.
     */
    //用于表示当前界面是属于哪种状态
    public static final int CHECK_STATE = 0;
    public static final int EDIT_STATE = 1;
    public static final int ALERT_STATE = 2;

    private ImageView mIvLeft;
    private TextView mTvCenter;
    private TextView mTvRight;

    private ListView listView;
    private ListViewAdapter adapter;// 数据源对象

    private DatabaseManage dm = null;// 数据库管理对象
    private Cursor cursor = null;

    private int id = -1;//被点击的条目

    private boolean needsyc = false;

    private SyncService mService = null;
    private boolean mBound = false;
    private int meunid = 3;

    private long month;


    private ArrayList<NoteItemtag> items;
    private ServiceConnection mConnection = new ServiceConnection() {

        //@Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SyncService.SyncBinder binder = (SyncService.SyncBinder) service;
            mService = binder.getService();
            mBound = true;
            if (needsyc) {
                mService.NotifySync();
                needsyc = false;
            }
        }

        //@Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notepad_main);

        initTitle();
        initResourceId();
        initListener();
        initData();
    }

    private void initData() {
        dm = new DatabaseManage(this);//数据库操作对象

        items = new ArrayList<NoteItemtag>();
        adapter = new ListViewAdapter(this, items);//创建数据源
        initAdapter();//初始化
        setListAdapter(adapter);//自动为id为list的ListView设置适配器

        Intent intent1 = getIntent();
        needsyc = intent1.getBooleanExtra("needsync", needsyc);
        Intent intent = new Intent(this, SyncService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    private void initListener() {

        //设置滑动监听器
        listView.setOnScrollListener(this);
        listView.setOnCreateContextMenuListener(new myOnCreateContextMenuListener());
    }

    private void initResourceId() {

        listView = getListView();//获取id为list的对象
    }

    private void initTitle() {
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mTvCenter = (TextView) findViewById(R.id.tv_center);
        mTvRight = (TextView) findViewById(R.id.tv_right);

        mIvLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvCenter.setText(R.string.mysapce_menu + meunid);
        mTvRight.setText("记录");
        mTvRight.setOnClickListener(new AddRecordListener());
    }

    //初始化数据源
    public void initAdapter() {
        items.clear();
        dm.open();//打开数据库操作对象

        month = 0;
        cursor = dm.selectAll();//获取所有数据

        cursor.moveToFirst();//将游标移动到第一条数据，使用前必须调用

        int count = cursor.getCount();//个数

        //ArrayList<String> items = new ArrayList<String>();
        //ArrayList<String> times = new ArrayList<String>();


        Calendar cal = Calendar.getInstance();

        for (int i = 0; i < count; i++) {
            if (cursor.getInt(cursor.getColumnIndex("syncstate")) == DatabaseManage.SYNC_STAT.DEL)
                continue;
            NoteItemtag item = new NoteItemtag();
            item.time = cursor.getLong(cursor.getColumnIndex("time"));
            cal.setTimeInMillis(item.time);
            if (month != cal.get(Calendar.MONTH)) {
                month = cal.get(Calendar.MONTH);
                NoteItemtag head = new NoteItemtag();
                head.time = -1;
                head.content = null;
                String txt = String.format("%d年%d月份", cal.get(Calendar.YEAR), month);
                head.title = txt;
                items.add(head);
            }
            item.id = cursor.getInt(cursor.getColumnIndex("id"));
            item.content = cursor.getString(cursor.getColumnIndex("content"));
            item.syncstate = cursor.getInt(cursor.getColumnIndex("syncstate"));
            items.add(item);

            cursor.moveToNext();//将游标指向下一个
        }
        dm.close();//关闭数据操作对象
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, SyncService.class);
        bindService(intent, mConnection, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onDestroy() {//销毁Activity之前，所做的事
        if (cursor != null) {
            cursor.close();//关闭游标
        }
        unbindService(mConnection);
        super.onDestroy();
    }

    //滑动事件
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

    }

    //---------------------------------------------------------------

    //响应长按弹出菜单的点击事件
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item
                        .getMenuInfo();
        //	HashMap<String, Object> map = List.get(menuInfo.position);
        //	Log.v("show", "shibai");
        if (adapter.getItem(menuInfo.position).time == -1)
            return false;

        NoteItemtag nt = adapter.getItem(menuInfo.position);
        switch (item.getItemId()) {
            case 0://删除
                try {
                    dm.open();
                    int i = dm.delete(adapter.getItemId(menuInfo.position));
                    dm.close();
                    adapter.removeListItem(menuInfo.position);//删除数据
                    adapter.notifyDataSetChanged();//通知数据源，数据已经改变，刷新界面
                    needsyc = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                break;
            case 1://修改
                try {
                    //用于Activity之间的通讯
                    Intent intent = new Intent();
                    intent.putExtra("id", nt.id);
                    intent.putExtra("state", ALERT_STATE);
                    intent.putExtra("time", /*cursor.getLong(cursor.getColumnIndex("time"))*/nt.time);
                    intent.putExtra("content", /*cursor.getString(cursor.getColumnIndex("content"))*/nt.content);
                    intent.setClass(NotepadActivity.this, NotepadEditActivity.class);
                    NotepadActivity.this.startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case 2://查看
                try {
                    Intent intent = new Intent();
                    intent.putExtra("id", nt.id);
                    intent.putExtra("time", nt.time + "");
                    intent.putExtra("content", nt.content);
                    intent.setClass(NotepadActivity.this, NotepadCheckActivity.class);
                    NotepadActivity.this.startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            default:
        }
        return super.onContextItemSelected(item);

    }

    //短按，即点击
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);


        NoteItemtag item = adapter.getItem(position);
        if (item.time < 1000)
            return;
        Intent intent = new Intent();
        intent.putExtra("state", CHECK_STATE);
        //intent.putExtra("hashcode", item.hashcode);
        intent.putExtra("id", item.id);
        intent.putExtra("time", item.time + "");
        intent.putExtra("content", item.content);

        intent.setClass(NotepadActivity.this, NotepadCheckActivity.class);
        NotepadActivity.this.startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case 0x99:
                    initAdapter();
                    break;


            }
        }
    }

    //长按
    public class myOnCreateContextMenuListener implements OnCreateContextMenuListener {

        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenuInfo menuInfo) {
            // TODO Auto-generated method stub
            final AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) menuInfo;

            int ps = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;

            if (adapter.getItem(ps).time == -1)
                return;

            menu.setHeaderTitle("");
            menu.setHeaderTitle("");
            //设置选项
            menu.add(0, 0, 0, "删除");
            menu.add(0, 1, 0, "修改");
            menu.add(0, 2, 0, "查看");
        }
    }

    //新建
    public class AddRecordListener implements OnClickListener {

        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = new Intent();
            intent.putExtra("state", EDIT_STATE);
            intent.setClass(NotepadActivity.this, NotepadEditActivity.class);
            NotepadActivity.this.startActivityForResult(intent, 0x99);
        }
    }
}