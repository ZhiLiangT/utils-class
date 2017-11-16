package com.bjb.mini_speaker.utils;

import java.util.ArrayList;
import java.util.List;

import com.bjb.mini_speaker.bean.AppInfo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/**
 * Created by tianzl on 2017/11/13.
 */

public class ApkTool {

	public static List<AppInfo> obAllApp(Context context) {
		List<AppInfo> apps=new ArrayList<AppInfo>();
		// 获取全部应用：
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> packageInfoList = packageManager
				.getInstalledPackages(0);
		// 判断是否系统应用：
		for (int i = 0; i < packageInfoList.size(); i++) {
			PackageInfo pak = (PackageInfo) packageInfoList.get(i);
			// 判断是否为系统预装的应用
			if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
				// 第三方应用
				AppInfo appInfo=new AppInfo();
				Log.i("ApkTool", "packageName:--"+pak.packageName);
				if (!pak.packageName.equals("com.bjb.mini_speaker")) {
					appInfo.setPkgName(pak.packageName);
					appInfo.setAppIcon(packageManager.getApplicationIcon(pak.applicationInfo));
					appInfo.setAppLabel(packageManager.getApplicationLabel(pak.applicationInfo).toString());
					apps.add(appInfo);
				}
			} else {
				// 系统应用
			}
		}
		return apps;
	}
	/**
     * 判断安装的应用之中是否安装了指定包名的应用
     * @param pkgName
     * @return
     */
    private boolean isPkgInstalled(String pkgName,Context context) {
        PackageInfo packageInfo = null;
        try {
          packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (NameNotFoundException e) {
          packageInfo = null;
          e.printStackTrace();
        }
        if (packageInfo == null) {
          return false;
        } else {
          return true;
        }
    }

}
