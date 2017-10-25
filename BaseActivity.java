package com.bbwhm.omeng.activity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public abstract class BaseActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//竖屏 
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// 避免输入法界面弹出后遮挡输入光标的问题
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		// 网页中的视频，上屏幕的时候，可能出现闪烁的情况
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		// 保持屏幕常亮
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// 隐藏标题栏 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 隐藏状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				              | View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		initContentView();
		initViews();
		initData();
		initEvents();
	}
	public abstract void initContentView();
	public abstract void initViews();
	public abstract void initData();
	public abstract void initEvents();
	
}
