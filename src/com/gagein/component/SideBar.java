package com.gagein.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.gagein.util.Log;

public class SideBar extends View {

	// 是否点击
	private boolean showBkg = false;
	OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	public static String[] letters = {"#", "A", "B", "C", "D", "E", "F", "G",
			"H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
			"U", "V", "W", "X", "Y", "Z", ""};
	// 选择的值
	int choose = -1;
	// 画笔
	Paint paint = new Paint();

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SideBar(Context context) {
		super(context);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 如果面板处于点击状态就将面板的背景色绘制为灰色
		if (showBkg) {
			canvas.drawColor(Color.parseColor("#00000000"));
		}
		// 获得Ｖｉｅｗ的高
		int height = getHeight();
		// 获得Ｖｉｅｗ的宽
		int width = getWidth();
		// 计算得出每一个字体大概的高度
		int singleHeight = height / letters.length;
		for (int i = 0; i < letters.length; i++) {
			// 设置锯齿
			paint.setAntiAlias(true);
			// 设置字体大小
			paint.setTextSize(28);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setColor(Color.parseColor("#FF8C44"));
			// 点击的字体和26个字母中的任意一个相等就
			if (i == choose) {
				// 绘制点击的字体的颜色为蓝色
				paint.setColor(Color.parseColor("#3399ff"));
				paint.setFakeBoldText(true);
			}
			// 得到字体的X坐标
			float xPos = width / 2 - paint.measureText(letters[i]) / 2;
			// 得到字体的Y坐标
			float yPos = singleHeight * i + singleHeight + 3;
			// 将字体绘制到面板上
			canvas.drawText(letters[i], xPos, yPos, paint);
			// 还原画布
			paint.reset();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		// 得到点击的状态
		final int action = event.getAction();
		// 点击的Y坐标
		final float y = event.getY();

		final int oldChoose = choose;
		// 监听
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		// 得到当前的值
		final int c = (int) (y / getHeight() * letters.length);
		switch (action) {
		// 按下
		case MotionEvent.ACTION_DOWN:
			// 将开关设置为true
			showBkg = true;
			Log.v("silen", "letter = " + letters[c]);
			if (oldChoose != c && listener != null) {
				if (c > 0 && c < letters.length) {
					listener.onTouchingLetterChanged(letters[c]);
					choose = c;
					// 刷新界面
					invalidate();
				}
			}
			break;
		// 移动
		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c && listener != null) {
				if (c > 0 && c < letters.length) {
					listener.onTouchingLetterChanged(letters[c]);
					choose = c;
					invalidate();
				}
			}
			break;
		// 松开 还原数据 并刷新界面
		case MotionEvent.ACTION_UP:
			showBkg = false;
			choose = -1;
			invalidate();
			break;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}
