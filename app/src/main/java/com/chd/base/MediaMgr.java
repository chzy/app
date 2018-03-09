package com.chd.base;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.chd.MediaMgr.utils.MFileFilter;
import com.chd.MediaMgr.utils.MediaFileUtil;
import com.chd.base.Entity.FileLocal;
import com.chd.base.Entity.FilelistEntity;
import com.chd.listener.DataCallBack;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo;
import com.chd.proto.FileInfo0;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.share.ShareUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class MediaMgr {

    private SQLiteDatabase db;
    private final String dbname = "CloudStore";
    private FTYPE _ftype;
    private boolean mExitTasksEarly;
    static ShareUtils shareUtils;
    //private   List<FileLocal> LocalUnits;
    //private HashMap<String ,Integer> LocalUnits;
    public final static String sZipFileMimeType = "application/zip";

    boolean contains = true;
    private Context context;

    public final int COLUMN_ID = 0;
    public final int COLUMN_PATH = 1;
    public final int COLUMN_SIZE = 2;
    public final int COLUMN_DATE = 3;
    private final String TAG = this.getClass().getName();

    int idx_cld ;

    public enum DBTAB {
        Dling, UPing, DLed, UPed
    }


    public List getLocalUnits() {
        return UILApplication.getFilelistEntity().getLocallist();
        //return this.LocalUnits;
    }


    private static String buildTable(DBTAB dbtab) {
        String tb = null;
        switch (dbtab) {
            case DLed:
                tb = "download_finish";
                break;
            case Dling:
                tb = "download_inter";
                break;
            case UPed:
                tb = "upload_finish";
            case UPing:
                tb = "upload_inter";
                break;
            default:
                break;

        }
        return tb;
    }


    public MediaMgr(Context context, FTYPE ftype) {
        this.context = context;
        shareUtils = new ShareUtils(context);
        //PicCache=new HashMap<Integer, FileInfo0>(20);
        _ftype = ftype;
        idx_cld=0;
        //if (LocalUnits==null)
        //	LocalUnits=new ArrayList<FileLocal>();
        /*HashSet<FileLocal> fileLocalHashSet=new HashSet<FileLocal>();
        fileLocalHashSet.contains("ddd");
		fileLocalHashSet.addAll(LocalUnits);*/
    }

    public MediaMgr(Context context) {
        this.context = context;
        shareUtils = new ShareUtils(context);
        //PicCache=new HashMap<Integer, FileInfo0>(20);
        _ftype = null;
        //if (LocalUnits==null)
        //	LocalUnits=new ArrayList<FileLocal>();
        //HashSet<FileLocal> fileLocalHashSet=new HashSet<FileLocal>();

    }

	/*public static HashMap<StoreUtil.FileCategory, FilenameExtFilter> filters = new HashMap<StoreUtil.FileCategory, FilenameExtFilter>();
*/

    public MFileFilter filters = new MFileFilter();
	/*public static HashMap<MediaFileUtil.FileCategory, Integer> categoryNames = new HashMap<MediaFileUtil.FileCategory, Integer>();

	static {
		categoryNames.put(MediaFileUtil.FileCategory.All,*//* R.string.category_all*//*1);
		categoryNames.put(MediaFileUtil.FileCategory.Music, *//*R.string.category_music*//*2);
		categoryNames.put(MediaFileUtil.FileCategory.Video, *//*R.string.category_video*//*3);
		categoryNames.put(MediaFileUtil.FileCategory.Picture, *//*R.string.category_picture*//*4);
		categoryNames.put(MediaFileUtil.FileCategory.Theme, *//*R.string.category_theme*//*5);
		categoryNames.put(MediaFileUtil.FileCategory.Doc, *//*R.string.category_document*//*6);
		categoryNames.put(MediaFileUtil.FileCategory.Zip, *//*R.string.category_zip*//*7);
		categoryNames.put(MediaFileUtil.FileCategory.Apk, *//*R.string.category_apk*//*8);
		categoryNames.put(MediaFileUtil.FileCategory.Other, *//*R.string.category_other*//*9);
		categoryNames.put(MediaFileUtil.FileCategory.Favorite, *//*R.string.category_favorite*//*10);
	}*/


    public void setCustomCategory(String[] exts, boolean contain) {
		/*//mCategory = FileCategory.Custom;
		if (filters.containsKey(StoreUtil.FileCategory.Custom)) {
			filters.remove(StoreUtil.FileCategory.Custom);
		}
		filters.put(StoreUtil.FileCategory.Custom, new FilenameExtFilter(exts));
		this.contains=contain;*/
        filters.setCustomCategory(Arrays.asList(exts), contain);
    }

    public void saveToSdcard(String filename, String content) throws IOException {
        context.getExternalFilesDir(Environment.DIRECTORY_DCIM);
    }

    public void open() {
        if (db == null) {
            db = new MedSqlHelper(context, dbname + shareUtils.getUserid() + ".db", 1).getWritableDatabase();
            return;
        }
        if (db.isOpen()) {
            return;
        } else {
            //db.close();
            db = new MedSqlHelper(context, dbname + shareUtils.getUserid() + ".db", 1).getWritableDatabase();
            return;
        }
        //ShareUtils shareUtils = new ShareUtils(context);;
    }

    public void close() {
        if (db != null) {
            if (db.isOpen()) {
                db.close();
            }
        }
    }



    public void anlayLocalUnits(final List<FileInfo> couldlist, FilelistEntity filelistEntity) {

        long t1, t0 = System.currentTimeMillis();
        //List<FileInfo> couldlist=filelistEntity.getBklist();

        List<FileInfo0> LocalUnits = filelistEntity.getLocallist();
        Collections.sort(couldlist, new ComparatorByName());
        FileInfo0 local_item;
        //FileInfo cld_item;

        int idx_cld = 0;
        int idx_lcl = 0;
        int vl = 0,unbackups=0;
        List<Integer> Unbak_idx_lst=filelistEntity.getUnbak_idx_lst();
        //FileInfo fileInfo;
        while (idx_cld < couldlist.size() && idx_lcl < LocalUnits.size()) {
            //fileInfo=couldlist.get(idx_cld);
            //fileInfo.setLastModified(TimeUtils.getDayWithTimeMillis0(fileInfo.getLastModified()));

            /*if ((fileInfo = couldlist.get(idx_cld)).getObjid().compareTo(LocalUnits.get(idx_lcl).getObjid()) < 0) {
                //fileInfo.setLastModified(TimeUtils.getDayWithTimeMillis0(fileInfo.getLastModified()));
                idx_cld++;
            } else if (couldlist.get(idx_cld).getObjid().compareTo(LocalUnits.get(idx_lcl).getObjid()) > 0) {
                idx_lcl++;
            }*/
            vl =  couldlist.get(idx_cld).getObjid().compareTo(LocalUnits.get(idx_lcl).getObjid());
            if (vl < 0) {
                //fileInfo.setLastModified(TimeUtils.getDayWithTimeMillis0(fileInfo.getLastModified()));
                idx_cld++;
            }
            else
            if (vl > 0) {
                Unbak_idx_lst.add(idx_lcl);
                idx_lcl++;
            }
            else {
                local_item = LocalUnits.get(idx_lcl);
                local_item.backuped = true;

                //filelistEntity.addbakups(local_item.getObjid(),/*local_item.getSysid()*/ 1);
                //fileInfo=couldlist.get(idx_cld);
                //fileInfo.setLastModified(TimeUtils.getDayWithTimeMillis0(fileInfo.getLastModified()));
                idx_cld++;
                idx_lcl++;
            }
        }
        t1=System.currentTimeMillis();
        Log.i(TAG, "anlayLocalUnits: compare cost :"+(t1-t0)+" ms");
       // Collections.sort(couldlist, new ComparatorByDate());
        filelistEntity.setBklist(couldlist);

        //filelistEntity.setLocallist(LocalUnits);
        t1 = System.currentTimeMillis();
        long z = t1 - t0;
        return;
    }


    public void anlayLocalUnits( final List<FileInfo> couldlist, FilelistEntity filelistEntity,int offset,int count) {

        long t1, t0 = System.currentTimeMillis();
        List<FileInfo0> LocalUnits = filelistEntity.getLocallist();

        List<Integer> Unbak_idx_lst=filelistEntity.getUnbak_idx_lst();
        if( Unbak_idx_lst.isEmpty() && couldlist.size()>1)
            Collections.sort(couldlist, new ComparatorByName());
        FileInfo0 local_item;
        int vl = 0;
        int idx_lcl=offset;
        String cldobj ,lclobj;
        //while (idx_cld+1 < couldlist.size() || idx_lcl+1 < LocalUnits.size())
        while (offset<count)
        {
            idx_cld=Math.min(idx_cld,couldlist.size()-1);
            idx_lcl=Math.min(idx_lcl,LocalUnits.size()-1);
            if (idx_lcl<0 || idx_cld<0)
                break;
            cldobj=couldlist.get(idx_cld).getObjid();
            lclobj=LocalUnits.get(idx_lcl).getObjid();
             vl =  cldobj.compareTo(lclobj);
            //vl =  couldlist.get(idx_cld).getObjid().compareTo(LocalUnits.get(idx_lcl).getObjid());
            if (vl < 0 ) {
                Unbak_idx_lst.add(idx_lcl);
                if (idx_cld>=couldlist.size()-1)
                {
                    idx_lcl++;
                }else
                    idx_cld++;
            }
            else
            if (vl > 0) {
                Unbak_idx_lst.add(idx_lcl);
                idx_lcl++;
            }
            else
                {
                local_item = LocalUnits.get(idx_lcl);
                local_item.backuped = true;
                //filelistEntity.addbakups(local_item.getObjid(),/*local_item.getSysid()*/ 1);
                idx_cld++;
                idx_lcl++;
            }
            offset++;
        }
        if (couldlist.isEmpty())
        {
            for( ; idx_lcl<count  ;idx_lcl++)
            {
                Unbak_idx_lst.add(idx_lcl);
            }
        }
        t1=System.currentTimeMillis();
        Log.i(TAG, "anlayLocalUnits: compare cost :"+(t1-t0)+" ms");
        // Collections.sort(couldlist, new ComparatorByDate());
       // filelistEntity.setBklist(couldlist);
        t1 = System.currentTimeMillis();
        long z = t1 - t0;
        return;
    }



    public void GetLocalFiles0(String[] exts, boolean include, final FilelistEntity filelistEntity, final DataCallBack dataCallBack) {
        //setCustomCategory(new String[]{"doc", "pdf", "xls", "zip", "rar"}, true);

        Uri fileUri= MediaStore.Files.getContentUri("external");
        final List LocalUnits = filelistEntity.getLocallist();
        //filelistEntity.getUnbak_idx_lst().clear();
        if (LocalUnits != null && !LocalUnits.isEmpty())
            return;

        String[] projection=new String[]{
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE,MediaStore.Files.FileColumns.SIZE,MediaStore.Files.FileColumns.DATE_MODIFIED,MediaStore.Files.FileColumns._ID
        };
        String selection="";
        for(int i=0;i<exts.length;i++)
        {
            if(i!=0)
            {
                selection=selection+" OR ";
            }
            selection=selection+ MediaStore.Files.FileColumns.DATA+" LIKE '%"+"."+exts[i]+"'";
        }
        //按时间递增顺序对结果进行排序;待会从后往前移动游标就可实现时间递减
        String sortOrder= MediaStore.Files.FileColumns.TITLE;
        //获取内容解析器对象
        ContentResolver resolver=context.getContentResolver();
        //获取游标
        final Cursor cursor=resolver.query(fileUri, projection, selection, null, sortOrder);
        if(cursor==null)
            return;
        //游标从最后开始往前递减，以此实现时间递减顺序（最近访问的文件，优先显示）
/*
        new Thread(new Runnable() {
            @Override
            public void run() {
*/

                int idx=0,count=0,offset=0;

                long t1,t0=System.currentTimeMillis();
                if(cursor.moveToLast())
                {
                    do{
                        //输出文件的完整路径
                        String fpath=cursor.getString(0);
                        //String name=
                        //Log.d("tag", data);
                        FileInfo0 fileLocal = new FileInfo0();
                        //fileLocal.setSysid(c.getInt(COLUMN_ID));
                        // File file = new File(fpath);
                        String objname = cursor.getString(1);
                        if (objname==null)
                            continue;
                        idx=fpath.lastIndexOf(objname);
                        if (idx<2)
                            continue;
                        fileLocal.setFilesize(cursor.getInt(2));
                       // fileLocal.setFtype(FTYPE.NORMAL);
                        fileLocal.setLastModified((int) cursor.getLong(3));
//                int pathid = c.getInt(COLUMN_ID);
                        String path =fpath.substring(0,idx);
                        int pathid = filelistEntity.addFilePath(path);
                        fileLocal.pathid=pathid;
                            //file = null;
                            //String objname=MediaFileUtil.getFnameformPath(c.getString(COLUMN_PATH));
                        fileLocal.setObjid(objname+fpath.substring(idx+objname.length()));
                        LocalUnits.add(fileLocal);
                        count++;
                        if ( count>1 && dataCallBack!=null && count%dataCallBack.callbackThreshold==0 ) {
                            t1=System.currentTimeMillis();
                            Log.i(TAG, "GetLocalFiles: cost time :"+ (t1 -t0 ) );
                            dataCallBack.success(LocalUnits, offset,count);
                            offset=count;
                        }
                       /* if (count>2000)
                            break;*/
                    }while(cursor.moveToPrevious());
                }
                cursor.close();
                dataCallBack.success(LocalUnits, offset,LocalUnits.size());
                t1=System.currentTimeMillis();
                Log.i(TAG, "GetLocalFiles: cost time :"+ (t1 -t0 ) );


   /*         }
        }).start();
   */
    }

    public void GetLocalFiles(MediaFileUtil.FileCategory fc, String[] exts, boolean include, FilelistEntity filelistEntity) {
        //setCustomCategory(new String[]{"doc", "pdf", "xls", "zip", "rar"}, true);
        long t1,t0=System.currentTimeMillis();
        List LocalUnits = filelistEntity.getLocallist();
        if (LocalUnits != null && !LocalUnits.isEmpty())
            return;
        setCustomCategory(exts, include);


        Cursor c = query(fc, MediaFileUtil.FileCategory.All, MediaFileUtil.SortMethod.name  /*date null*/);

        if (c != null) {
            while (c.moveToNext()) {
                String fpath = c.getString(COLUMN_PATH);
                if (filters.contains(fpath) == false) {
                    continue;
                }
                else {
                    if (1==1)
                        continue;
                }
                    FileLocal fileLocal = new FileLocal();
                //fileLocal.setSysid(c.getInt(COLUMN_ID));
                File file = new File(c.getString(COLUMN_PATH));
                String objname = file.getName();
                String path = file.getParent();
                //fileLocal.setFilesize(file.length());
                fileLocal.setFtype(FTYPE.NORMAL);
                fileLocal.setLastModified((int) c.getLong(COLUMN_DATE));
//                int pathid = c.getInt(COLUMN_ID);
                int pathid = filelistEntity.addFilePath(path);
                fileLocal.setPathid(pathid);
                file = null;
                //String objname=MediaFileUtil.getFnameformPath(c.getString(COLUMN_PATH));
                fileLocal.setObjid(objname);
                LocalUnits.add(fileLocal);
                //LocalUnits.put(fileLocal.fname,fileLocal);
            }
            c.close();
        }
        t1=System.currentTimeMillis();
        Log.i(TAG, "GetLocalFiles: cost time :"+ (t1 -t0 ) );
        if (c == null) {
            Log.e(TAG, "fail to query uri");
        }

    }

    public List<FileLocal> GetPartLocalFiles(MediaFileUtil.FileCategory fc, String[] exts, boolean include, int begin, int max) {
        List locals = new ArrayList<FileLocal>();
        setCustomCategory(exts, include);
        Cursor c = query(/*MediaFileUtil.FileCategory.File*/fc, MediaFileUtil.FileCategory.All, MediaFileUtil.SortMethod.date);
        int count = 0, total = 0;
        while (c.moveToNext()) {
            String fpath = c.getString(COLUMN_PATH);
            if (filters.contains(fpath) == false) {
                continue;
            }
            count++;
            if (count < begin)
                continue;
            FileLocal fileLocal = new FileLocal();
            //fileLocal.setSysid(c.getInt(COLUMN_ID));
            //fileLocal.fname= MediaFileUtil.getFnameformPath(c.getString(COLUMN_PATH));
            locals.add(fileLocal);
            total++;
            if (total >= max)
                break;
        }
        c.close();
        if (c == null) {
            Log.e("", "fail to query uri");
        }
        return locals;
    }

    public boolean queryLocalInfo(int sysid, FileInfo0 fileInfo0) {

        //MediaFileUtil.FileCategory fc0= MediaFileUtil.FileCategory.File;
        boolean ret = false;
        MediaFileUtil.FileCategory fc;
        switch (fileInfo0.getFtype()) {
            case MUSIC:
                fc = MediaFileUtil.FileCategory.Music;
                break;
            case PICTURE:
                fc = MediaFileUtil.FileCategory.Picture;
                break;
            case VIDEO:
                fc = MediaFileUtil.FileCategory.Video;
                break;
            case RECORD:
                fc= MediaFileUtil.FileCategory.Record;
                break;
            case NORMAL:
                fc = MediaFileUtil.FileCategory.Other;
                break;
            default:
                fc = MediaFileUtil.FileCategory.File;
        }
        Uri uri = getContentUriByCategory(fc);
        String selection = buildSelectionByCategory(MediaFileUtil.FileCategory._ID);
        String sortOrder = null;

        if (uri == null) {
            Log.e("", "invalid uri, category:" + fc.name());
            return ret;
        }
        String[] columns = new String[]{
                MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED
        };
        Cursor cursor = context.getContentResolver().query(uri, columns, selection, new String[]{"" + sysid}, sortOrder);
        while (cursor.moveToNext()) {
            fileInfo0.setFilePath(cursor.getString(COLUMN_PATH));
            if (fileInfo0.getObjid() != null)
                Log.d(TAG, "is a  remote obj");
            fileInfo0.setObjid(MediaFileUtil.getNameFromFilepath(cursor.getString(COLUMN_PATH)));
            fileInfo0.setFilesize(cursor.getInt(COLUMN_SIZE));
            fileInfo0.setLastModified(cursor.getInt(COLUMN_DATE));
           // fileInfo0.setSysid(sysid);

            ret = true;

        }
        cursor.close();
        return ret;
    }

    public FileInfo0 getPiceParam(int picId) {
        return null;
    }


    protected class SortBydesc implements Comparator<Object> {
        @Override
        public int compare(Object o1, Object o2) {
            if ((Integer) o1 > (Integer) o2) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private FileInfo0 getUploadeItem(String objid, FileInfo0 info) {
        FileInfo0 file = null;
        Cursor cursor = db.rawQuery("select * from upload_finished" + " where type=? objid=" + objid, /*wheresection*/null);
        if (cursor.moveToNext()) {
            //file.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));
            file.setFilename(cursor.getString(cursor.getColumnIndex("name")));
            file.setObjid(objid);
            //	file.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));

        }
        cursor.close();
        return null;
    }

    //public abstract ArrayList<Integer> anlayLocalUnits(List<FileInfo0> couldlist);


   /* public void setUploadinfFile0(FileInfo0 entity) {
        ContentValues values = new ContentValues();
        values.put("objid", entity.getObjid());
        //values.put("offset",entity.getOffset());
        db.update("upload_inter", values, "sysid=?", new String[]{entity.getSysid() + ""});
    }*/


    private Uri getContentUriByCategory(MediaFileUtil.FileCategory cat) {
        Uri uri;
        String volumeName = "external";
        switch (cat) {
            case Theme:
            case Doc:
            case Zip:
            case Apk:
            case Other:
            case Record:
            case File:
                uri = MediaStore.Files.getContentUri(volumeName);
                break;
            case Music:
//                uri = MediaStore.Audio.Media.getContentUri(volumeName);
                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                break;
            case Video:
//                uri = MediaStore.Video.Media.getContentUri(volumeName);
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                break;
            case Picture:
//                uri = MediaStore.Images.Media.getContentUri(volumeName);
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                break;
            default:
                uri = null;
        }
        return uri;
    }

    private String buildSortOrder(MediaFileUtil.SortMethod sort) {
        if (sort == null)
            return null;
        String sortOrder = null;
        switch (sort) {
            case name:
                sortOrder = MediaStore.Files.FileColumns.TITLE + " asc";
                break;
            case size:
                sortOrder = MediaStore.Files.FileColumns.SIZE + " asc";
                break;
            case date:
                sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " desc";
                break;
            case type:
                sortOrder = MediaStore.Files.FileColumns.MIME_TYPE + " asc, " + MediaStore.Files.FileColumns.TITLE + " asc";
                break;
            case id:
                sortOrder = MediaStore.Files.FileColumns._ID + " desc ";
                break;

        }
        return sortOrder;
    }


    private String buildSelectionByCategory(MediaFileUtil.FileCategory cat) {
        String selection = null;
        switch (cat) {
            case Theme:
                selection = MediaStore.Files.FileColumns.DATA + " LIKE '%.mtz'";
                break;
            case Doc:
                selection = buildDocSelection();
                break;
            case Zip:
                selection = "(" + MediaStore.Files.FileColumns.MIME_TYPE + " == '" + sZipFileMimeType + "')";
                break;
            case Apk:
                selection = MediaStore.Files.FileColumns.DATA + " LIKE '%.apk'";
                break;
			/*case File:
				selection = MediaStore.Files.FileColumns._ID +" = ?";
				break;*/
            case _ID:
                selection = MediaStore.Files.FileColumns._ID + " = ?";
                break;
            default:
                selection = null;
        }
        return selection;
    }

    private String buildDocSelection() {
        StringBuilder selection = new StringBuilder();
        Iterator<String> iter = MediaFileUtil.sDocMimeTypesSet.iterator();
        while (iter.hasNext()) {
            selection.append("(" + MediaStore.Files.FileColumns.MIME_TYPE + "=='" + iter.next() + "') OR ");
        }
        return selection.substring(0, selection.lastIndexOf(")") + 1);
    }

    public Cursor query(MediaFileUtil.FileCategory fc, MediaFileUtil.FileCategory cond, MediaFileUtil.SortMethod sort) {
        Uri uri = getContentUriByCategory(fc);
        String selection = buildSelectionByCategory(cond);
        String sortOrder = buildSortOrder(sort);

        if (uri == null) {
            Log.e("", "invalid uri, category:" + fc.name());
            return null;
        }

        String[] columns = new String[]{
                MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED
        };
        return context.getContentResolver().query(uri, columns, selection, null, sortOrder);
    }


    public static void fileScan(String fName, Context context1) {
        Uri data = Uri.parse("file:///" + fName);
        context1.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
    }


    public void add2mediaStore(FileInfo0 info0) {

        if (1 == 1) {
            UILApplication.getFilelistEntity().getLocallist().clear();
            fileScan(info0.getFilePath(), context);
            return;
        }


        ContentResolver localContentResolver = context.getContentResolver();

//		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "picPath")));

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "picPath")));

        Cursor cursor = localContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "select max(_ID)", null, null);
        //Cursor cursor=localContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "select _ID,_DATA ",new String[] {"group by _ID having max(ID)"}, null);
        cursor.moveToFirst();
        int mid = cursor.getInt(0);
        String[] projection = {
                MediaStore.Images.Media._ID
                , MediaStore.Images.Media.DATE_MODIFIED
                , MediaStore.Images.Media.SIZE
                , MediaStore.Images.Media.DATA
        };

        cursor.close();
        cursor = localContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.Media._ID + "= ?", new String[]{String.valueOf(mid)}, null);
        cursor.moveToFirst();


		/*try {
			MediaStore.Images.Media.insertImage(localContentResolver,info0.getFilePath(),"","");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/

        ContentValues localContentValues = new ContentValues();
        localContentValues.put(MediaStore.Images.Media.DATA, cursor.getString(3));
        localContentValues.put(MediaStore.Images.Media.SIZE, cursor.getInt(2));
        localContentValues.put(MediaStore.Images.Media.DATE_MODIFIED, cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)));
        localContentValues.put("mime_type", "image/jpeg");
        cursor.close();

        //int d=localContentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media._ID +"= ?",new String[]{String.valueOf(mid)});

		/*localContentValues.put(MediaStore.Images.Media.DATA, info0.getFilePath());
		localContentValues.put(MediaStore.Images.Media.SIZE, info0.getFilesize());
		localContentValues.put(MediaStore.Images.Media.DATE_MODIFIED,info0.getLastModified());
		localContentValues.put("mime_type", "image/jpeg");
*/
        //MediaStore.Images.Media.insertImage()


        Uri uri = localContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);

        //long id = ContentUris.parseId(uri);
        //String path=uri.getPath();


        cursor = localContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "select last_insert_rowid()", null, null);
        int id = 0;
        if (cursor.moveToFirst())
            id = cursor.getInt(0);
        //return  id;
        return;
    }


///////////////////////// 下载记录管理部分////////////////////////////

    // 通过文件名找到downloaded记录
    public boolean QueryDownloadedFile(FileInfo0 info0, boolean autopen) {
        if (autopen)
            open();
        String path = info0.getFilePath() == null ? new ShareUtils(context).getStorePathStr() + File.separator +
                info0.getObjid() : info0.getFilePath();
        if (new File(path).exists())
            return true;
        boolean ret = QueryDBEntity(DBTAB.DLed, info0);
        if (autopen)
            close();
        return ret;
    }

    // 通过文件名找到uploaded记录
    public boolean QueryUploadedFile(FileInfo0 info0, boolean autopen) {
        if (autopen)
            open();
        boolean ret = QueryDBEntity(DBTAB.UPed, info0);
        if (autopen)
            close();
        return ret;
    }


	/*public List<FileInfo0> getUpLoadingFiles(){
		return getFileDataDBEntitiesU("upload_inter", false);
	}
	
	public List<FileInfo0> getDownloadedFiles(){
		return getFileDataDBEntities("download_finish", true);
	}*/
	/*public List<FileInfo0> getUpLoadedFiles(){
		return getFileDataDBEntitiesU("upload_finish", true);
	}*/

    private FileInfo0 getFileDataDBEntity(String db1, String objid, boolean finished) {
        FileInfo0 file = null;
        Cursor cursor = db.rawQuery("select * from " + db1 + " where objid=" + objid, null);
        if (cursor.moveToNext()) {
            //file.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));
            //file.setFilename(getpath(cursor.getString(cursor.getColumnIndex("name"))));
            file.setFilename(cursor.getString(cursor.getColumnIndex("name")));
            file.setObjid(objid);
            //if (!finished)
            //	file.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));

        }
        cursor.close();
        return null;
    }

    public boolean QueryDBEntity(DBTAB dbtab, FileInfo0 info0) {
        boolean ret = false;
        if (!info0.isSetObjid())
            return ret;
        if (!info0.isSetFtype() && _ftype == null)
            return ret;
        int type = info0.isSetFtype() ? info0.getFtype().getValue() : _ftype.getValue();

        Cursor cursor = db.rawQuery("select * from " + buildTable(dbtab) + " where type= " + type + " and objid='" + info0.getObjid() + "'", null);

        boolean finished = (dbtab.equals(DBTAB.UPing) || dbtab.equals(DBTAB.Dling));
        if (cursor.moveToNext()) {
            if (info0.getFilePath() == null)
                info0.setFilePath(cursor.getString(cursor.getColumnIndex("path")));
            //if (info0.getOffset()<1  && !finished )
            //	info0.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));
            if (!info0.isSetLastModified())
                info0.setLastModified(cursor.getInt(cursor.getColumnIndex("time")));
            if (!info0.isSetFilesize() && !finished && dbtab.equals(DBTAB.Dling))
                info0.setFilesize(cursor.getLong(cursor.getColumnIndex("size")));
            ret = true;
        }
        cursor.close();
        return ret;
    }

    private List<FileInfo0> QueryDBUnits(DBTAB dbtab, FTYPE ftype, int max) {
        //boolean ret=false;
        String sel = "";
        if (ftype != null)
            sel = "where type= " + ftype.getValue();
        String sql = "select objid,size,offset,time from " + buildTable(dbtab) + sel + " order by time ";
        Cursor cursor = db.rawQuery(sql, null);
        List<FileInfo0> list = new ArrayList<FileInfo0>();
        boolean finished = (dbtab.equals(DBTAB.UPing) || dbtab.equals(DBTAB.Dling));
        int count = cursor.getCount();
        while (cursor.moveToNext() && max > 0) {
            FileInfo0 info0 = new FileInfo0();
            info0.setFilePath(cursor.getString(cursor.getColumnIndex("objid")));
            info0.setOffset(cursor.getInt(cursor.getColumnIndex("offset")));
            info0.setLastModified(cursor.getInt(cursor.getColumnIndex("time")));
            info0.setFilesize(cursor.getLong(cursor.getColumnIndex("size")));
            try {
                int cid = 0;
                cid = cursor.getColumnIndexOrThrow("sysid");
               // info0.setSysid(cursor.getInt(cid));
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            max--;
            list.add(info0);
        }
        cursor.close();
        return list;
    }

    public void addUpLoadingFile(FileInfo0 entity) {
        ContentValues values = new ContentValues();
        //values.put(/*"hash"*/"sysid",/*entity.getFilePath().hashCode()*/entity.getSysid());
        values.put("objid", entity.getObjid());

        values.put("path", entity.getFilePath());

        values.put("size", entity.getFilesize());
        values.put("type", (entity.getFtype()).getValue());
        values.put("offset", 0);
        values.put("time", /*TimeUtils.getCurrentTimeInLong()*/entity.getLastModified());
        db.insertWithOnConflict("upload_inter", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /*public void setUploadStatus(FileInfo0 entity) {
        ContentValues values = new ContentValues();
        values.put("sysid", entity.getSysid());
        values.put("objid", entity.getObjid());
        values.put("offset", entity.getOffset());
        int ret = db.update("upload_inter", values, "type=? and objid=?", new String[]{String.valueOf(entity.getFtype()), entity.getObjid()});
    }*/


    public void addDownloadingFile(FileInfo0 entity) {
        ContentValues values = new ContentValues();
        //values.put("objid", entity.getObjid());
        values.put("objid", entity.getObjid());
        values.put("size", entity.getFilesize());
        values.put("path", entity.getFilePath());
        values.put("offset", 0);
        values.put("type", (entity.getFtype()).getValue());
        values.put("time", entity.getLastModified());
        db.insertWithOnConflict("download_inter", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void setDownloadStatus(FileInfo0 entity) {
        ContentValues values = new ContentValues();
        //values.put("sysid",entity.getSysid());
        values.put("objid", entity.getObjid());
        values.put("offset", entity.getOffset());
        db.update("download_inter", values, "type=? and objid=?", new String[]{String.valueOf(entity.getFtype()), entity.getObjid()});
    }

    public void finishTransform(DBTAB dbtab, FileInfo0 info0) {
		/*boolean dl=dbtab.equals(DBTAB.DLed);
		boolean up=dbtab.equals(DBTAB.UPed);
		*/
        switch (dbtab) {
            case DLed:
                addDownloadedFile(info0);
                return;
            case UPed:
                addUpLoadedFile(info0);
                return;

        }
    }

    public List<FileInfo0> getUpLoadTask(int max) {
        return QueryDBUnits(DBTAB.UPing, null, max);
    }

    public List<FileInfo0> getDlLoadTask(int max) {
        return QueryDBUnits(DBTAB.Dling, null, max);
    }

    public void addDownloadedFile(FileInfo0 entity) {
        ContentValues values = new ContentValues();
        values.put("objid", entity.getObjid());
        values.put("path", entity.getFilePath());
        values.put("size", entity.getFilesize());
        values.put("time", entity.getLastModified());
        values.put("type", entity.getFtype().getValue());
        deleteUpLoadingFile(entity.getObjid());
        db.insertWithOnConflict("upload_finish", null, values, SQLiteDatabase.CONFLICT_IGNORE);

    }

    public void addUpLoadedFile(FileInfo0 entity) {
        ContentValues values = new ContentValues();
        values.put("objid", entity.getObjid());
        values.put("size", entity.getFilesize());
        values.put("path", entity.getFilePath());
       // values.put("sysid", entity.getSysid());
        values.put("type", entity.getFtype().getValue());
        values.put("time", entity.getLastModified());
        deleteUpLoadingFile(entity.getObjid());
        db.insertWithOnConflict("upload_finish", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }


    private void deleteDownloadingFile(String objid) {
        deleteFileDataDBEntity("download_inter", objid);
    }

    private void deleteUpLoadingFile(String objid) {
        deleteFileDataDBEntity("upload_inter", objid);
    }

    private void deleteDownloadedFile(String objid) {
        deleteFileDataDBEntity("download_finish", objid);

    }

    private void deleteUpLoadedFile(String objid) {
        deleteFileDataDBEntity("upload_finish", objid);

    }

    private void deleteFileDataDBEntity(String db1, String objid) {
        db.delete(db1, "objid=?", new String[]{objid + ""});
    }


    private class ComparatorObj0 implements Comparator {

        public int compare(Object arg0, Object arg1) {
            FileInfo0 item0 = (FileInfo0) arg0;
            FileInfo0 item1 = (FileInfo0) arg1;
            int flag = item0.getObjid().compareTo(item1.getObjid());
            return flag;
        }
    }

    private class ComparatorByName implements Comparator {

        public int compare(Object arg0, Object arg1) {
            String item0 = ((FileInfo)arg0).getObjid();
            String item1 = ((FileInfo)arg1).getObjid();
           // int flag = item0.compareTo(item1);
            int flag = item1.compareTo(item0);
            return flag;
        }
    }

    private class ComparatorByDate implements Comparator {

        public int compare(Object arg0, Object arg1) {
            FileInfo item0 = (FileInfo) arg0;
			/*if (item0.getLastModified()>29991230)
				item0.setLastModified(TimeUtils.getDayWithTimeMillis0(item0.getLastModified()));*/
            FileInfo item1 = (FileInfo) arg1;
			/*if (item1.getLastModified()>29991230)
				                      //1467270158
				item1.setLastModified(TimeUtils.getDayWithTimeMillis0(item1.getLastModified()));*/
            int flag = item0.getLastModified() - item1.getLastModified();
            return flag;
        }
    }
}
