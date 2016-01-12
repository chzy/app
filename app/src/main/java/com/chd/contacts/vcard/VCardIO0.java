package com.chd.contacts.vcard;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.chd.TClient;
import com.chd.contacts.ui.Contacts;
import com.chd.proto.FTYPE;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;


public class VCardIO0 {
	private Context context;

	public VCardIO0(Context context) {
		this.context = context;
	}

	/**
	 * 导入联系人信息
	 * 
	 * @param fileName
	 *            要导入的文件
	 * @param replace
	 *            是否替换先有联系人
	 * @param activity
	 *            主窗口
	 */
	public void doImport(final String fileName, final boolean replace,
			final Contacts activity) {
		new Thread() {
			@Override
			public void run() {
				try {

					File vcfFile = new File(fileName);

					final BufferedReader vcfBuffer = new BufferedReader(
							new FileReader(fileName));
					final long maxlen = vcfFile.length();

					// 后台执行导入过程
					new Thread(new Runnable() {
						public void run() {
							long importStatus = 0;
							Contact parseContact = new Contact();
							try {
								long ret = 0;
								do {
									ret = parseContact.parseVCard(vcfBuffer);
									if (ret < 0) {
										break;
									}
									parseContact.addContact(
											context.getApplicationContext(), 0,
											replace);
									importStatus += parseContact.getParseLen();
									// 更新进度条
									activity.updateProgress((int) (100 * importStatus / maxlen));

								} while (true);
								activity.updateProgress(100);

							} catch (IOException e) {
							}
						}
					}).start();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}


	public void doImport0(final String fileName, final boolean replace,
						 final Contacts activity) {
		new Thread() {
			@Override
			public void run() {
				try {

					final File vcfFile = new File(fileName);

					//final BufferedReader vcfBuffer = new BufferedReader(
					//		new FileReader(fileName));
					final long filelen =vcfFile.length();

					// 后台执行导入过程
					new Thread(new Runnable() {
						public void run() {

								//Contact parseContact = new Contact();
							try {
								final FileInputStream inputStream =new FileInputStream(vcfFile);
								int readlen=0;long flen=0;
								/*final byte[] readbuf=new byte[1024];
								long ret = 0;
								*//*ContentResolver cResolver = context.getContentResolver();
								String[] projection = { ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID };
								String selection = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME + " = ?";
								String[] selParams = new String[] {ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, peopleData.displayName };
								Cursor people = cResolver.query(ContactsContract.Data.CONTENT_URI, projection, selection,
										selParams, null);*//*

								//ContentValues values = new ContentValues();
								//Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
								final ContentResolver cResolver = context.getContentResolver();
								String[] projection = { ContactsContract.Contacts._ID };
								String selection = ContactsContract.Data._ID + " = ? ";
								String[] selParams = new String[] {"json id" };
								final Cursor people;
								people = cResolver.query(
										ContactsContract.Contacts.CONTENT_URI, projection,
										selection, selParams, null);
								if (replace)
								{

								}*/


								//values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
								//values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
								//values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
								//getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
								//values.clear();
								/*values.put(Data.RAW_CONTACT_ID, rawContactId);
								values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
								values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, num);
								values.put(Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME);*/
								//getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

							/*	if (people != null && people.moveToFirst()) {
									Log.i("people.getString(0)", people.getString(0));
									if (replace) {
										do {
											setId(people.getString(1));
											key = getId();
											RemoveContact(cResolver, key);
										} while (people.moveToNext());
									} else {
										people.close();
										return 0;
									}
								}
								people.close();*/
								int rdbyte=0,dimcout=0,pos=0,len=0;
								ByteBuffer readbuf=ByteBuffer.allocate(1024);
								String tag;
								Gson gson=new Gson();
								Contact parseContact = new Contact();
								while (( rdbyte= inputStream.read()) != -1) {

									if (rdbyte == '}' )
									{
										if (dimcout !=1)
										{
											pos=0;
											len=0;
											dimcout=0;
											continue;
										}
										readbuf.put((byte)rdbyte);

										readbuf.flip();
										byte[] strbuf=new byte[readbuf.remaining()];
										readbuf.get(strbuf,0,strbuf.length);
										tag = new String(strbuf);
										strbuf=null;
										//SmsItem item = gson.fromJson(tag, SmsItem.class);
										String dd=gson.toJson(tag);
										pos = 0;
										len = 0;
										dimcout=0;
										parseContact.addContact0(context,tag,true);
									}

									if (rdbyte=='{') {
										dimcout++;
										readbuf.clear();
									}
									if (dimcout==1)
									{ readbuf.put( (byte)rdbyte);
										pos++;
										len++;
									}
								}

								/*do {
									flen  =filelen   + (long)readlen;




									// 更新进度条
									activity.updateProgress((int) (100 * filelen / filelen));

								} while ( (readlen=inputStream.read())!=-1 );
								activity.updateProgress(100);*/

							} catch (IOException e) {
							}
						}
					}).start();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void doExport(final String fileName, final Contacts activity) {
		new Thread() {
			@Override
			public void run() {
				try {
					final BufferedWriter vcfBuffer = new BufferedWriter(
							new FileWriter(fileName));

					final ContentResolver cResolver = context
							.getContentResolver();
					String[] projection = { ContactsContract.Contacts._ID };
					final Cursor allContacts;
					//if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ECLAIR) {
					allContacts = cResolver.query(
							ContactsContract.Contacts.CONTENT_URI, projection,
							null, null, null);
					//}
					if (!allContacts.moveToFirst()) {
						allContacts.close();
					}

					final long maxlen = allContacts.getCount();
					// 线程中执行导出
					new Thread(new Runnable() {
						public void run() {
							long exportStatus = 0;
							String id = null;
							Contact parseContact = new Contact();
							try {
								do {
									id = allContacts.getString(0);
									parseContact.getContactInfoFromPhone(id, cResolver);
									parseContact.writeVCard(vcfBuffer);
									++exportStatus;
									// 更新进度条
									activity.updateProgress((int) (100 * exportStatus / maxlen));
								} while (allContacts.moveToNext());
								activity.updateProgress(100);
								vcfBuffer.close();
								allContacts.close();



							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

	/**
	 * 导出联系人信息
	 *
	 * @param fileName
	 *            存放导出信息的文件
	 * @param activity
	 *            主窗口
	 */

	public void upload(final String fileName, final Contacts activity) {
		new Thread() {
			@Override
			public void run() {
				try {
					/*final BufferedWriter vcfBuffer = new BufferedWriter(
							new FileWriter(fileName));*/

					final ContentResolver cResolver = context.getContentResolver();
					String[] projection = { ContactsContract.Contacts._ID };
					final Cursor allContacts;
					//if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ECLAIR) {
					allContacts = cResolver.query(
							ContactsContract.Contacts.CONTENT_URI, null,
							null, null, null);
					//}
					if (!allContacts.moveToFirst()) {
						allContacts.close();

					}

					final long maxlen = allContacts.getCount();
					// 线程中执行导出
					new Thread(new Runnable() {
						public void run() {
							long exportStatus = 0;
							String id = null;
							Contact parseContact = new Contact();
							try {
								File f=new File(fileName);
								f.deleteOnExit();
								TClient tClient =TClient.getinstance();
								TClient.TFilebuilder filebuilder;
								filebuilder = tClient.new TFilebuilder(f.getName(), FTYPE.ADDRESS);
								filebuilder.ApplyObj();

								//long total=f.length();
								FileOutputStream outputStream = new FileOutputStream(f);
								byte[] readbuf;
								int bflen=0;
								long flen=0;
								boolean appendret=false;
								int cls=allContacts.getColumnCount();
								String line;
								String[] names=new String[cls];
								for (int i=0;i<cls;i++)
								{
									names[i]=allContacts.getColumnName(i);
								}
								do {

									JSONArray array = new JSONArray();
									JSONObject jsonObj = new JSONObject();
									for(int i=0;i<cls;i++) {
										jsonObj.put(names[i],allContacts.getString(i));
									}
									array.put(jsonObj);
									line=array.toString();
									array=null;
									readbuf=(line+"\r\n").getBytes("UTF8");
																		flen+=readbuf.length;
									outputStream.write(readbuf);
									++exportStatus;
									// 更新进度条
									activity.updateProgress((int) (100 * exportStatus / maxlen));
								} while (allContacts.moveToNext() );
								activity.updateProgress(100);
								allContacts.close();
								filebuilder.Commit(null);
								outputStream.flush();
								outputStream.close();
								activity.updateProgress(100);
							} catch (IOException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(context,"备份到云端失败",Toast.LENGTH_SHORT);
							}
						}
					}).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}



}
