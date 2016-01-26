package com.chd.notepad.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chd.notepad.ui.db.FileDBmager;
import com.chd.notepad.ui.item.NoteItemtag;
import com.chd.yunpan.R;

import java.util.Calendar;
import java.util.List;




public class ListViewAdapter extends BaseAdapter {

	
	//private List<String> listItems;
	//private List<String> listItemTimes;
	//private HashMap<Integer,NoteItemtag> listItems;
	private List<NoteItemtag> listItems;

	private  static  int month ;
	private LayoutInflater inflater;
	private FileDBmager fileDBmager;
	
	public ListViewAdapter(Context context, List<NoteItemtag> listItems0){
		this.listItems = listItems0;
		month=0;
		//this.listItemTimes = times;
		inflater = (LayoutInflater)
				context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		fileDBmager=new FileDBmager(context);
	}
	
	
/*	*
	 * 往列表添加条目
	 * @param item*/

	/*
	public void addListItem(String item, String time){
		//listItems.add(item);
		//listItemTimes.add(time);
		
	}*/
	
	/**
	 * 删除指定位置的数据
	 * @param position
	 */
	public void removeListItem(int position){
		listItems.remove(position);
		//listItemTimes.remove(position);
	}

	
	/**
	 * 获取列表的数量
	 */
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	/**
	 * 根据索引获取列表对应索引的内容
	 */
	public NoteItemtag getItem(int position) {
		// TODO Auto-generated method stub
		return listItems.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return listItems.get(position).id;
	}

	/*public int getItemhashcode(int position)
	{
		return listItems.get(position).hashcode;
	}
*/
	private void hidenView(View v)
	{
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
		params.height=0;
		params.width=0;
		v.setLayoutParams(params);
		v.setVisibility(View.GONE);
	}

	private void hidenLinerView(View v)
	{
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v.getLayoutParams();
		params.height=0;
		params.width=0;
		v.setLayoutParams(params);
		v.setVisibility(View.GONE);
	}
	
	/**
	 * 通过该函数显示数据
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		if(convertView == null ){
			convertView = inflater.inflate(R.layout.notepad_list_item,null);
		}
		NoteItemtag tag=listItems.get(position);
		if (tag==null)
			return convertView ;
		//if (tag.syncstate== DatabaseManage.SYNC_STAT.DEF)
		//	return convertView ;

		TextView txt_day = (TextView) convertView.findViewById(R.id.note_date_day);
		TextView txt_hour = (TextView) convertView.findViewById(R.id.note_date_hour);
		TextView text = (TextView) convertView.findViewById(R.id.note_txt);
		View line = convertView.findViewById(R.id.note_line);

		if (position == 0)
		{
			RelativeLayout.LayoutParams lparams = (RelativeLayout.LayoutParams) line.getLayoutParams();
			lparams.addRule(RelativeLayout.BELOW,R.id.notepad_title_shap);
			
			line.setLayoutParams(lparams);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(tag.getStamp()*1000L);
		if (tag.isHead )
		{
			ImageView view = (ImageView) convertView.findViewById(R.id.notepad_title_bg);

			TextView head = new TextView(convertView.getContext());
			RelativeLayout rv= (RelativeLayout) convertView.findViewById(R.id.note_top_tile);
			RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
			rv.addView(head, params);

			head.setGravity(Gravity.CENTER | Gravity.LEFT);
			head.setTextSize(20);
			head.setTextColor(Color.rgb(51, 51, 51));
			head.setText("  " + tag.getDateStr());

			hidenView(view);
			hidenView(txt_day);
			hidenView(txt_hour);
			hidenLinerView(text);
			return convertView;
		}
		
		txt_day = (TextView) convertView.findViewById(R.id.note_date_day);
		txt_hour = (TextView) convertView.findViewById(R.id.note_date_hour);
		txt_day.setText((cal.get(Calendar.MONTH)+1)+"月"+cal.get(Calendar.DAY_OF_MONTH) + "日");
		txt_hour.setText("" + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + " " + (cal.get(Calendar.AM_PM) > 0 ? "PM" : "AM"));
		//time1=TimeUtils.getTimeTxt(tag.time,"kk:mm");
		String content=fileDBmager.readFile(tag.get_fname());
		text.setText(content);
		
		return convertView;
	}

}

