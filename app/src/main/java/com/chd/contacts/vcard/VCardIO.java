package com.chd.contacts.vcard;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chd.MediaMgr.utils.MediaFileUtil;
import com.chd.TClient;
import com.chd.base.Entity.FileLocal;
import com.chd.base.Ui.ActiveProcess;
import com.chd.proto.FTYPE;
import com.chd.proto.FileInfo0;
import com.chd.service.RPCchannel.upload.FileUploadInfo;
import com.chd.service.RPCchannel.upload.FileUploadManager;
import com.chd.service.RPCchannel.upload.UploadOptions;
import com.chd.service.RPCchannel.upload.listener.OnUploadListener;
import com.chd.service.RPCchannel.upload.progressaware.ProgressBarAware;
import com.chd.service.SyncLocalFileBackground;
import com.chd.yunpan.application.UILApplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;


public class VCardIO {
	private final String TAG = this.getClass().getName();
	private Context context;

	public VCardIO(Context context) {
		this.context = context;
	}

	/**
	 * 导入联系人信息
	 *
	 * @param handler
	 * @param fileName 要导入的文件
	 * @param replace  是否替换先有联系人
	 * @param activity 主窗口
	 * @param netSize
	 */
	public void doImport(final Handler handler, final String fileName, final boolean replace,
	                     final ActiveProcess activity, final int netSize) {
		new Thread() {
			@Override
			public void run() {
				try {
					final BufferedReader vcfBuffer = new BufferedReader(
							new FileReader(fileName));
					// 后台执行导入过程
					long importStatus = 0;
					int len = 0;
					Contact parseContact = new Contact();
					long ret = 0;
					do {
						len++;
						ret = parseContact.parseVCard(vcfBuffer);
						if (ret < 0) {
							break;
						}
						parseContact.addContact(
								context.getApplicationContext(), 0,
								replace);
						Log.d("添加成功:",len+"");
						importStatus += parseContact.getParseLen();
						// 更新进度条
						activity.updateProgress(100 * len / netSize);
					} while (true);
				} catch (Exception e) {
					Log.e("lmj", "联系人出错");
				} finally {
					activity.toastMain("恢复完成");
					activity.finishProgress();
					getLocalSize(handler);
				}
			}
		}.start();
	}


	public void getLocalSize(final Handler handler) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final ContentResolver cResolver = context
						.getContentResolver();
				String[] projection = {ContactsContract.Contacts._ID};
				final Cursor allContacts;
				allContacts = cResolver.query(
						ContactsContract.Contacts.CONTENT_URI, projection,
						null, null, null);
				if (allContacts != null) {
					int maxlen = allContacts.getCount();
					Message msg = new Message();
					msg.what = 998;
					msg.obj = maxlen;
					handler.sendMessage(msg);
				}
			}
		}).start();
	}


	public void getNetSize(final String fileName, final Handler handler) {
				int size = 0;
				Message msg = new Message();
				msg.what = 999;
				try {
					TClient tClient = TClient.getinstance();
					HashMap<String,String> descmap=tClient.queryAttribute(fileName, FTYPE.ADDRESS);
					String lines = descmap.get("lines");
					size = Integer.parseInt(lines);

					msg.what = 999;
					msg.obj = size;
					handler.sendMessage(msg);
				} catch (IOException e) {

				} catch (Exception e1) {
					Log.e(TAG, e1.getMessage());
					return;
				}

               /* } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }*/

	}


	public void doExport(final String fileName, final ActiveProcess activity, final Handler handler) {
		try {
		            /*if (1==1)
		                upload( fileName,  activity);*/
			final BufferedWriter vcfBuffer = new BufferedWriter(
					new FileWriter(fileName));

			final ContentResolver cResolver = context
					.getContentResolver();
			String[] projection = {ContactsContract.Contacts._ID};
			final Cursor allContacts;
			//if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ECLAIR) {
			allContacts = cResolver.query(
					ContactsContract.Contacts.CONTENT_URI, projection,
					null, null, null);
			//}
			if (!allContacts.moveToFirst()) {
				allContacts.close();
				return;
			}

			final int maxlen = allContacts.getCount();
			// 线程中执行导出
			{
				long exportStatus = 0;
				String id = null;
				Contact parseContact = new Contact();
					do {
						id = allContacts.getString(0);
						parseContact.getContactInfoFromPhone(id, cResolver);
						parseContact.writeVCard(vcfBuffer);
						++exportStatus;
						Log.d("上传了:",""+exportStatus);
						// 更新进度条
						activity.updateProgress((int) (100 * exportStatus / maxlen));
					} while (allContacts.moveToNext());
					vcfBuffer.flush();
					vcfBuffer.close();
					allContacts.close();
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						File tmpFile=new File(fileName);
						FileLocal fileLocal = new FileLocal();
						int pathid = UILApplication.getFilelistEntity().addFilePath(tmpFile.getParent());
						fileLocal.setPathid(pathid);
						fileLocal.setFtype(FTYPE.ADDRESS);
						fileLocal.setObjid(tmpFile.getName());
						boolean overwrite = true;
						boolean resume = true;
						UploadOptions options = new UploadOptions(overwrite, resume);
						final MaterialDialog.Builder builder = new MaterialDialog.Builder(activity);
						builder.content("正在上传:0%");
						builder.progress(false, 100);
						final MaterialDialog build = builder.build();
						build.show();
						HashMap<String, String> desc = new HashMap<String, String>();
						desc.put("lines", "" + maxlen);
						 FileUploadManager manager=FileUploadManager.getInstance();
						manager.uploadFile(new ProgressBarAware(build), desc, fileLocal, new OnUploadListener() {
							@Override
							public void onError(FileUploadInfo uploadData, int errorType, String msg) {
								build.dismiss();
								activity.toastMain("上传失败");
							}

							@Override
							public void onSuccess(FileUploadInfo uploadData, Object data) {
								build.dismiss();
								activity.toastMain("上传成功");
								Message msg = new Message();
								msg.what = 999;
								msg.obj = maxlen;
								handler.sendMessage(msg);

							}
						}, options);
					}
				});
			}
		} catch (IOException e) {
			activity.finishProgress();
			e.printStackTrace();
		}


	}

	/**
	 * 导出联系人信息
	 *
	 * @param filePath 存放导出信息的文件
	 * @param activity 主窗口
	 */

	public boolean upload(final String filePath, final ActiveProcess activity, int lines) {


		// 线程中执行
		//new Thread(new Runnable() {
		//public void run() {
		FileInfo0 info = new FileInfo0();
		info.setObjid(MediaFileUtil.getNameFromFilepath(filePath));
		info.setFilePath(filePath);
		info.setFtype(FTYPE.ADDRESS);
		HashMap<String, String> desc = new HashMap<String, String>();
		desc.put("lines", "" + lines);
		return new SyncLocalFileBackground(context).uploadFileOvWrite(info, activity, desc,null);
		//}
		//}).start();
	}

	/**
	 * 下载联系人信息
	 *
	 * @param filePath 存放导出信息的文件
	 * @param activity 主窗口
	 */

	public boolean download(final String filePath, final ActiveProcess activity) {
		// 线程中执行
		FileInfo0 info = new FileInfo0();
		info.setObjid(MediaFileUtil.getNameFromFilepath(filePath));
		info.setFilePath(filePath);
		info.setFtype(FTYPE.ADDRESS);
		return new SyncLocalFileBackground(context).downloadBigFile(info, activity, null);

	}


}
