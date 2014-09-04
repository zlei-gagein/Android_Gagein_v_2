package com.gagein.ui.main;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gagein.R;
import com.gagein.util.CommonUtil;

/**
 * navigation
 * 
 * @author wufei
 * 
 */
public class NavigationActivity extends Activity implements OnClickListener{

	private ViewPager viewpager_navigation;
	private int numNavigation;
	private ArrayList<View> imageViewsList;
	private ImageView[] imageCircleViews = null;
	private ImageView[] imageViews = null;
	private ImageView imageView = null;
	private SlideImageAdapter slideImageAdapter = null;
	private ImagePageChangeListener imagePageChangeListener = null;
	private Button loginBtn;
	private LinearLayout linerlayout_circle;
	private int [] imageResId;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation);
		mContext = this;

		initView();
		setOnClickListener();
	}
	
	private void initView() {
		
		loginBtn = (Button) findViewById(R.id.loginBtn);
		linerlayout_circle = (LinearLayout) findViewById(R.id.linerlayout_circle);
		viewpager_navigation = (ViewPager) findViewById(R.id.viewpager_navigation);
		setViewPager();
		
		if (CommonUtil.isTablet(mContext)) {//TODO
			
		}
	}

	private void setOnClickListener() {
		loginBtn.setOnClickListener(this);
	}

	public void onClick(View v) {
		if (v == loginBtn) {
			Intent intent = new Intent();
			CommonUtil.addActivity(this);
			intent.setClass(mContext, LoginActivity.class);
			startActivity(intent);
		}
	}

	private void setViewPager() {
		
		imageResId = new int[] { R.layout.view_navigation_one, R.layout.view_navigation_two, R.layout.view_navigation_three, R.layout.view_navigation_four };
		numNavigation = imageResId.length;
		imageViewsList = new ArrayList<View>();
		for (int i = 0; i < numNavigation; i++) {
			View view = LayoutInflater.from(mContext).inflate(imageResId[i], null);
			imageViewsList.add(view);
		}

		imageCircleViews = new ImageView[numNavigation];
		imageViews = new ImageView[numNavigation];
		linerlayout_circle.removeAllViews();

		for (int i = 0; i < numNavigation; i++) {
			imageCircleViews[i] = getCircleImageLayout(i);
			linerlayout_circle.addView(imageCircleViews[i]);
		}

		slideImageAdapter = new SlideImageAdapter(imageViewsList);
		imagePageChangeListener = new ImagePageChangeListener();
		viewpager_navigation.setAdapter(slideImageAdapter);
		viewpager_navigation.setOnPageChangeListener(imagePageChangeListener);
	}

	/**
	 * 生成圆点图片区域布局对象
	 * 
	 * @param index
	 * @return
	 */
	public ImageView getCircleImageLayout(int index) {
		
		imageView = new ImageView(mContext);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(8, 0, 8, 0);
		imageView.setLayoutParams(layoutParams);

		imageViews[index] = imageView;

		if (index == 0) {
			imageViews[index].setBackgroundResource(R.drawable.circle_selected);// 默认选中第一张图片
		} else {
			imageViews[index].setBackgroundResource(R.drawable.circle_unselected);
		}

		return imageViews[index];
	}

	/**
	 * 导航页滑动适配器
	 */
	class SlideImageAdapter extends PagerAdapter {

		ArrayList<View> imagePageViews;

		public SlideImageAdapter(ArrayList<View> imagePageViews) {
			this.imagePageViews = imagePageViews;
		}

		@Override
		public int getCount() {
			return imagePageViews.size();
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View arg0, int position, Object arg2) {
			((ViewPager) arg0).removeView(imagePageViews.get(position));
		}

		@Override
		public Object instantiateItem(View arg0, int position) {
			((ViewPager) arg0).addView(imagePageViews.get(position));

			return imagePageViews.get(position);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public void finishUpdate(View arg0) {
		}
	}

	/**
	 * 导航页滑动监听器
	 */
	private class ImagePageChangeListener implements OnPageChangeListener {
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int index) {
			for (int i = 0; i < numNavigation; i++) {
				imageCircleViews[index]
						.setBackgroundResource(R.drawable.circle_selected);

				if (index != i) {
					imageCircleViews[i]
							.setBackgroundResource(R.drawable.circle_unselected);
				}
			}
		}
	}

}
