package com.chd.photo.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class PicGridItemLayout extends RelativeLayout
{

	public PicGridItemLayout(Context context) {
		super(context);
	}

	public PicGridItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PicGridItemLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
		
		// Children are just made to fill our space.
        int childWidthSize = getMeasuredWidth();
        int childHeightSize = getMeasuredHeight();
        //高度和宽度一样
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
