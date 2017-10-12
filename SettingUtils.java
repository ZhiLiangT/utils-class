package com.bbwhm.omeng.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SettingUtils {
	private static Properties props = null;
	private static String filePath = null;
	public static Properties init(String _filepath) {
		filePath = _filepath;
		props = new Properties();
		InputStream in = SettingUtils.class.getResourceAsStream(filePath);
		try {
			props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}
	
	public static String getFilePath() {
		return filePath;
	}
	
	public static String get(String key) {
		return props.getProperty(key);
	}
	
	public static String get(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}
}
