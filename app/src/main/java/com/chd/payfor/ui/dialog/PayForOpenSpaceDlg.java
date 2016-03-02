package com.chd.payfor.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.chd.yunpan.R;

public class PayForOpenSpaceDlg extends Dialog implements android.view.View.OnClickListener
{
	
	private Context context = null;
	private TextView mTextContent;

	public PayForOpenSpaceDlg(Context context, int theme) 
	{
		super(context, theme);
	}

	protected PayForOpenSpaceDlg(Context context, boolean cancelable, OnCancelListener cancelListener) 
	{
		super(context, cancelable, cancelListener);
	}

	public PayForOpenSpaceDlg(Activity context) 
	{
		super(context);
		
		this.context = context;
	}
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlg_openspace_payfor);
		
		mTextContent = (TextView) findViewById(R.id.dlg_openspace_payfor_content);
		findViewById(R.id.dlg_openspace_payfor_cancel).setOnClickListener(this);
		findViewById(R.id.dlg_openspace_payfor_submit).setOnClickListener(this);
	}
	
	public void showMyDialog(String msg)
	{
		showMyDialog();
		
		if (msg != null)
		{
			mTextContent.setText(msg);
		}
	}
		
	public void showMyDialog(){
		Window window = getWindow();
		window.setGravity(Gravity.CENTER);
		window.setWindowAnimations(R.style.mystyle); // 添加动画
		show();
		WindowManager windowManager = ((Activity)context).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = display.getWidth();
		getWindow().setAttributes(params);
		window.setBackgroundDrawableResource(R.drawable.background_dialog);
	}
		
	private OnConfirmListener onConfirmListener = null;

	public interface OnConfirmListener {
		void confirm();
		void cancel();
	}

	public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
		this.onConfirmListener = onConfirmListener;
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) {
		case R.id.dlg_openspace_payfor_cancel:
		{
			this.dismiss();
			if (onConfirmListener != null)
			{
				onConfirmListener.cancel();
			}
		}
			break;
		case R.id.dlg_openspace_payfor_submit:
		{
			this.dismiss();
			if (onConfirmListener != null)
			{
				onConfirmListener.confirm();
			}
		}
			break;
		default:
			break;
		}
	}

}
