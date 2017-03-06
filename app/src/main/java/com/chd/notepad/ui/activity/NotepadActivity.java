package com.chd.notepad.ui.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
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

import com.chd.TClient;
import com.chd.base.backend.SyncTask;
import com.chd.notepad.service.SyncBackground;
import com.chd.notepad.ui.adapter.ListViewAdapter;
import com.chd.notepad.ui.db.FileDBmager;
import com.chd.notepad.ui.item.NoteItemtag;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.share.ShareUtils;
import com.google.gson.Gson;
import com.lockscreen.pattern.GuideGesturePasswordActivity;
import com.lockscreen.pattern.UnlockGesturePasswordActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;


public class NotepadActivity extends ListActivity implements OnScrollListener {
    /**
     * Called when the activity is first created.
     */
    //用于表示当前界面是属于哪种状态
    public static final int CHECK_STATE = 0;
    public static final int EDIT_STATE = 1;
    public static final int ALERT_STATE = 2;
    private final int MODIFY_NOTPAD = 0x1001;
    FileDBmager fileDBmager;
    private ImageView mIvLeft;
    private TextView mTvCenter;
    private TextView mTvRight;
    private ListView listView;
    private ListViewAdapter adapter;// 数据源对象
    private Cursor cursor = null;
    private int id = -1;//被点击的条目
    private int meunid = 3;
    private long month;
    private SyncTask syncTask;
    private List<FileInfo0> cloudUnits;
    private ArrayList<NoteItemtag> items;
    private SyncBackground syncBackground;
    private ProgressDialog dialog;
    private  String  savepath;
    private Handler mHandler= new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    dialog.dismiss();
                    initData();
                    break;

                case SyncBackground.SUCESS:
                    //同步成功
                    Log.d("liumj", "执行完毕");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            initData();
                        }
                    }, 500);

                    break;
            }
        }

    };

    private void runfrist() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                if (syncTask == null)
                    syncTask = new SyncTask(NotepadActivity.this, FTYPE.NOTEPAD);
                //未备份文件 ==  backedlist . removeAll(localist);
                if (cloudUnits == null || cloudUnits.isEmpty())
                    // 0-100 分批取文件
                    cloudUnits = syncTask.getCloudUnits(0, 10000);

                if (syncBackground==null) {
                    syncBackground = new SyncBackground(NotepadActivity.this, mHandler, cloudUnits, savepath);
                    syncBackground.start();
                }

                String file;
                for (FileInfo fileInfo : cloudUnits) {
                    FileInfo0 fileInfo0 = new FileInfo0(fileInfo);
                    file = savepath + File.separator + fileInfo0.getObjid();
                    if (fileInfo.filesize==0)
                    {
                        try {

                            if (!TClient.getinstance().delObj(fileInfo0.getObjid(), fileInfo0.getFtype()))
                                Log.e("ddd", fileInfo0.getObjid() + " " + fileInfo0.getFtype());
                            continue;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (new File(file).exists() == false) {
                        fileInfo0.setFilePath(file);
                        syncTask.download(fileInfo0, null, false, dialog);
                        Log.d("NotepadActivity", "download note :" + fileInfo0.getObjid());
                    }
                }
                mHandler.sendEmptyMessage(1);
            }

        });
        thread.start();
    }

    private boolean isShow=true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        savepath = new ShareUtils(this).getNotepadDir();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.notepad_main);

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("正在加载");
        dialog.show();
        runfrist();
        initTitle();
        initResourceId();
        initListener();

    }






    private void initData() {
        //dm = new DatabaseManage(this);//数据库操作对象

        items = new ArrayList<NoteItemtag>();
        adapter = new ListViewAdapter(this, items);//创建数据源
        initAdapter();//初始化
        setListAdapter(adapter);//自动为id为list的ListView设置适配器

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
        //dm.open();//打开数据库操作对象
        fileDBmager = new FileDBmager(this);
        month = 0;
        Calendar cal = Calendar.getInstance();
        Iterator<String> iterator = fileDBmager.getLocallist();


        //for (int i = 0; i < count; i++)
        while (iterator.hasNext()) {
            NoteItemtag item = new NoteItemtag();
            String fname = iterator.next();
            fname = fname.substring(0, fname.length() - 4);
            item.set_fname(fname);
            cal.setTimeInMillis(item.getStamp());
            if (month != cal.get(Calendar.MONTH)) {
                month = cal.get(Calendar.MONTH);
                NoteItemtag head = new NoteItemtag();
                //head.time = -1;
                head.content = null;
                String txt = String.format("%d年%d月份", cal.get(Calendar.YEAR), month);
                head.isHead = true;
                head.setDateStr(txt);
                items.add(head);
            }
            // item.id = cursor.getInt(cursor.getColumnIndex("id"));
//            item.content = cursor.getString(cursor.getColumnIndex("content"));
            //item.syncstate = cursor.getInt(cursor.getColumnIndex("syncstate"));
            items.add(item);

            // cursor.moveToNext();//将游标指向下一个
        }
        // dm.close();//关闭数据操作对象
        adapter.notifyDataSetChanged();

    }


    @Override
    protected void onResume() {
        super.onResume();
        //gson = new Gson();
       // syncBackground = new SyncBackground(this, mHandler);

        //if (syncBackground==null)
        //    syncBackground = new SyncBackground(this, mHandler,cloudUnits,savepath);

/*
        Log.d("ntp", "" + syncBackground.getState());
        if (syncBackground.getState()==Thread.State.NEW )
                syncBackground.start();*/
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {//销毁Activity之前，所做的事
        if (cursor != null) {
            cursor.close();//关闭游标
        }

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
        if (adapter.getItem(menuInfo.position).isHead)
            return false;

        NoteItemtag nt = adapter.getItem(menuInfo.position);
        switch (item.getItemId()) {
            case 0://删除
                try {
                    // dm.open();
                    //int i = dm.delete(adapter.getItemId(menuInfo.position));
                    //dm.close();
                    if (fileDBmager.delFile(nt.get_fname()))
                        Log.d("notepad",nt.get_fname()+"删除文件成功！");
                    adapter.removeListItem(menuInfo.position);//删除数据
                    adapter.notifyDataSetChanged();//通知数据源，数据已经改变，刷新界面
                    dialog.show();
                    //if (syncBackground==null) {
                   //     syncBackground=new SyncBackground(this, mHandler,cloudUnits);
                   //     syncBackground.start();
                   // }
                    syncBackground.wakeup(1);
                    cloudUnits.clear();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                break;
            case 1://修改
                try {
                    //用于Activity之间的通讯
                    Intent intent = new Intent();
                    //intent.putExtra("id", nt.id);
                    intent.putExtra("state", ALERT_STATE);
                    //intent.putExtra("fname", /*cursor.getLong(cursor.getColumnIndex("time"))*/nt.get_fname());
                    // intent.putExtra("content", /*cursor.getString(cursor.getColumnIndex("content"))*/nt.content);
                    intent.putExtra("item", nt);
                    intent.setClass(NotepadActivity.this, NotepadEditActivity.class);
                    NotepadActivity.this.startActivityForResult(intent, MODIFY_NOTPAD);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case 2://查看
                try {
                    Intent intent = new Intent();
                    //intent.putExtra("id", nt.id);
                    //intent.putExtra("time",nt.get_fname());
                    //intent.putExtra("content", nt.content);
                    intent.putExtra("item", nt);
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
        if (item.isHead)
            return;
        Intent intent = new Intent();
        intent.putExtra("item", item);

        intent.setClass(NotepadActivity.this, NotepadCheckActivity.class);
        NotepadActivity.this.startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {

            switch (requestCode) {
                case MODIFY_NOTPAD:
                    dialog.show();
                    syncBackground.wakeup(1);
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

            if (adapter.getItem(ps).isHead)
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
            NotepadActivity.this.startActivityForResult(intent, MODIFY_NOTPAD);
        }
    }
}