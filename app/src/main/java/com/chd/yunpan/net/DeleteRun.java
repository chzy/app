package com.chd.yunpan.net;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.chd.TClient;

import java.util.List;

public class DeleteRun implements Runnable {

	/*
	private Integer fid;
	private Integer cid;
	private int type;
	private Integer pid;
	*/
	private Context context;
	private List<?> list;
	private Handler handler;

	/*public DeleteRun(int type, Integer fid, Integer cid, Context context,
			Handler handler, Integer pid) {
		this.pid = pid;
		this.fid = fid;
		this.cid = cid;
		this.context = context;
		this.handler = handler;
		this.type = type;
	}*/

	public DeleteRun( Context context,
					 Handler handler, List<?> list) {
/*
		this.pid = pid;
		this.fid = fid;
		this.cid = cid;
		this.type = type;
*/

		this.context = context;
		this.handler = handler;
		this.list=list;
	}

	@Override
	public void run() {
		boolean b = false;
		Message msg = new Message();
		/*String objid = null;
		if (fid == 0) {
			objid = String.valueOf(cid);
			type = 2;
		} else {
			objid = String.valueOf(fid);
			type = 1;
		}*/
		//DeleteFileEntity entity = DeleteFileUtil.down(context, id);
		try {
			b = TClient.getinstance().equals(list);
		} catch (Exception e) {
			e.printStackTrace();
		}

		msg.obj = b;
		if (b)
			msg.what = 2;
		else
			msg.what = -1;
		handler.sendMessage(msg);
		/*
		if(entity==null)
		{
			Message message = new Message();

			message.what = -1;
			handler.sendMessage(message);
		}
		else {
			System.out.println(entity);
			System.out.println(type + "tttttttt");
			Message message = new Message();
			entity.setFid(fid);
			entity.setCid(cid);
			entity.setType(type);
			entity.setPid(pid);
			entity.setState(true);
			message.what = 2;
			message.obj = entity;
			handler.sendMessage(message);
		}*/
	}
}
