package com.gagein.component;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewAdapterScreen extends ImageView {

	public ImageViewAdapterScreen(Context context) {
		super(context);
	}
	
	public ImageViewAdapterScreen(Context context, AttributeSet attrs) {
	    super(context, attrs);
	  }

	  public ImageViewAdapterScreen(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	  }
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		try {
		      Drawable drawable = getDrawable();
		      if (drawable == null) {
		        setMeasuredDimension(0, 0);
		      } else {
		        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
		        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
		        if (measuredHeight == 0 && measuredWidth == 0) { //Height and width set to wrap_content
		          setMeasuredDimension(measuredWidth, measuredHeight);
		        } else if (measuredHeight == 0) { //Height set to wrap_content
		          int width = measuredWidth;
		          int height = width *  drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth();
		          setMeasuredDimension(width, height);
		        } else if (measuredWidth == 0){ //Width set to wrap_content
		          int height = measuredHeight;
		          int width = height * drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
		          setMeasuredDimension(width, height);
		        } else { //Width and height are explicitly set (either to match_parent or to exact value)
		          setMeasuredDimension(measuredWidth, measuredHeight);
		        }
		      }
		    } catch (Exception e) {
		      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		    }
	}

}
