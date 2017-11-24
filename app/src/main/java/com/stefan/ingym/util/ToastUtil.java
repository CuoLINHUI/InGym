/**
 * @ClassName:
 * @Description:
 * @Author Stefan
 * @Date
 */
package com.stefan.ingym.util;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * @ClassName: ToastUtil
 * @Description: 吐司实用工具类
 * @Author Stefan
 * @Date 2017/9/21 16:16
 */
public class ToastUtil {
    /**
     * 打印吐司的方法
     * Toast.makeText(context, text, duration).show();
     * @param ctx  上下文环境
     * @param msg  打印的文本内容
     */
    public static void show(Context ctx, String msg) {  // 为了方便外部调用，设置成共有的静态方法
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }
}
