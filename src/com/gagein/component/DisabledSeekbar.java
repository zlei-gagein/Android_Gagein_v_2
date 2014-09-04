package com.gagein.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class DisabledSeekbar extends SeekBar {

	public DisabledSeekbar(Context context) {
		super(context);
	}
	
	public DisabledSeekbar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
	}

	public DisabledSeekbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

	
}
