package com.stefan.ingym.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil {
	
	private static SharedPreferences sp;

	/**
	 *	写入用户选中的状态的方法到sp保存的文件中 
	 * @param context	上下文环境
	 * @param key		存储节点的名称
	 * @param value		存储节点的值 	 Boolean类型值
	 */
	public static void putBoolean(Context context, String key, boolean value) {
		// 为空时在为其赋值
		if (sp == null) {
			// getSharedPreferences(name, mode)--> （存储节点文件名称， 读写状态）
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, value).commit();
	}
		
	/**
	 * 从保存的文件中读出用户选中的状态的方法
	 * @param context	上下文环境
	 * @param key		存储节点的名称
	 * @param defValue	没有此节点时的默认值
	 * @return 			返回此节点读取到的结果或者是默认值
	 */
	public static boolean getBoolean(Context context, String key, boolean defValue) {
		// 为空时在为其赋值
		if (sp == null) {
			// getSharedPreferences(name, mode)--> （存储节点文件名称， 读写状态）
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getBoolean(key, defValue);
	}
	
	/**
	 * 写入用户设置的密码到sp保存的文件中
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putString(Context context, String key, String value) {
		// 赋值前判断sp是否为空
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).commit();
	}
	
	/**
	 * 读取用户设置的密码
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static String getString(Context context, String key, String defValue) {
		// 赋值前判断sp是否为空
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getString(key, defValue);
	}
	
	/**
	 * 写入用户选择的索引号sp保存的文件中
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putInt(Context context, String key, int value) {
		// 赋值前判断sp是否为空
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putInt(key, value).commit();
	}
	
	/**
	 * 读取用户选择的索引号
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static int getInt(Context context, String key, int defValue) {
		// 赋值前判断sp是否为空
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getInt(key, defValue);
	}

	/**
	 * 从Sp中删除指定节点中的数据
	 * @param context	上下文环境
	 * @param key		需要删除的节点名称
     */
	public static void remove(Context context, String key) {
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().remove(key).commit();
	}
}
