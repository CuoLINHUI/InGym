/**
 * @ClassName:
 * @Description:
 * @Author Stefan
 * @Date
 */
package com.stefan.ingym.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @ClassName: ToolUtils
 * @Description: 工具类
 * @Author Stefan
 * @Date 2017/9/21 15:27
 */

public class ToolUtils {

    /**
     * 获得一个SharedPreferences的对象 ,并指定它的名称和访问类型
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("com.stefan.ingym", context.MODE_PRIVATE);
    }

    /**
     * 向SharedPreferences插入数据
     */
    public static void putShareData(Context context, String key, String value) {
        // 获得SharedPreferences对象
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        // 获得SharedPreferences的编辑器
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 获得SharePreferences指定键的值
     */
    public static String getShareData(Context context, String key, String defValue) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(key, defValue);
    }

    /**
     * 清除SharePreferences数据
     */
    public static void cleanShareData(Context context, String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

}
