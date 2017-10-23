package com.stefan.ingym.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

/**
 *	实现获取焦点的自定义的TextView
 */
public class FocusTextView extends TextView {
	// 使用在通过java代码创建的控件中
	public FocusTextView(Context context) {
		super(context);
	}
	
	// 由系统调用（上下文环境构造方法， 属性）
	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	// 由系统调用（上下文环境构造方法， 属性, 布局文件中自定义样式文件构造方法）
	public FocusTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	// 重写获取焦点的方法，该方法由系统调用，调用的时候默认就能获取焦点
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		return true;
	}

}
