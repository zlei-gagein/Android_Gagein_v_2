package com.gagein.ui.main;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gagein.R;
import com.gagein.util.CommonUtil;
import com.gagein.util.Constant;

/**
 * map
 * 
 * @author silen
 * 
 */
public class ImageViewActivity extends Activity implements OnClickListener{
	
	private ImageView image;
	private LinearLayout layout;
	private String URL = "";
	private String imageType = "";
	private int mapWidth;
	private int mapHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		
		initView();
		initData();
	}

	protected void initView() {
		image = (ImageView) findViewById(R.id.image);
		layout = (LinearLayout) findViewById(R.id.layout);
		image.setOnClickListener(this);
		layout.setOnClickListener(this);
	}

	protected void initData() {
		URL = getIntent().getStringExtra(Constant.URL);
		imageType = getIntent().getStringExtra(Constant.IMAGE_TYPE);
		if (imageType.equals(Constant.IMAGE_TYPE_MAP)) {
			mapWidth = CommonUtil.getDeviceHeight(ImageViewActivity.this);
			mapHeight = CommonUtil.getDeviceWidth(ImageViewActivity.this);
			String mapAddress = CommonUtil.setMapSize(URL, mapWidth, mapHeight);
			CommonUtil.loadImage(mapAddress, image);
		} else if (imageType.equals(Constant.IMAGE_TYPE_REVENUE)) {
			CommonUtil.loadImage(URL, image);
		}
	}
	
	@Override
	public void onClick(View v) {
		finish();
	}

}
