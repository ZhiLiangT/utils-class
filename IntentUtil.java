package com.bbwhm.omeng.utils;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class IntentUtil {
	Intent intent;
	Context context;
	
	private IntentUtil(Context context) {
		intent = new Intent();
		this.context = context;
	}
	
	private IntentUtil(Context context, Class<?> cls) {
		intent = new Intent(context, cls);
		this.context = context;
	}
	
	private IntentUtil(Context context, String packgeName) {
		PackageManager pm = context.getPackageManager();
		intent = pm.getLaunchIntentForPackage(packgeName);
		this.context = context;
	}
	
	/**
	 * 启动该应用下的Activity
	 * @param context
	 * @param cls
	 * @return
	 */
	public static IntentUtil init(Context context, Class<?> cls) {
		IntentUtil util = new IntentUtil(context, cls);
		return util;
	}
	
	/**
	 * 启动其他应用
	 * @param context
	 * @param packgeName
	 * @return
	 */
	public static IntentUtil init(Context context, String packgeName) {
		IntentUtil util = new IntentUtil(context, packgeName);
		return util;
	}
	
	public static IntentUtil init(Context context) {
		IntentUtil util = new IntentUtil(context);
		return util;
	}
	
	public IntentUtil action(String _action) {
		intent.setAction(_action);
		return this;
	}
	
	public IntentUtil put(String key, String value) {
		intent.putExtra(key, value);
		return this;
	}
	
	public void start() {
		context.startActivity(intent);
	}
}
