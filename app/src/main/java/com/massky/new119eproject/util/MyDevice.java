package com.massky.new119eproject.util;

import android.util.DisplayMetrics;

import com.massky.new119eproject.widget.ApplicationContext;

/**
 * 
 * @author Administrator
 *
 */
public class MyDevice {
	/**
	 * device's width
	 */
	public static final int WIDTH;
	/**
	 * device's height
	 */
	public static final int HEIGHT;
	/**
	 * 
	 */
	static{//静态块代码的好处就是只被执行一次，类加载时
		DisplayMetrics dm = ApplicationContext.getInstance().getResources().getDisplayMetrics();
		WIDTH = dm.widthPixels;
		HEIGHT = dm.heightPixels;
	}
}
