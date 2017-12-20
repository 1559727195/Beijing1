package com.massky.new119eproject.biz;

import com.massky.new119eproject.R;
import com.massky.new119eproject.model.SmartItemInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 
 * @author Administrator
 * 
 */
public class Biz {

	protected static final String TAG = "JSON";


	/**
	 * get all smart item
	 *
	 * @return
	 */
	public static List<SmartItemInfo> getSmartItems() {
		List<SmartItemInfo> items = new ArrayList<SmartItemInfo>();

		SmartItemInfo item1 = new SmartItemInfo();
		item1.name = "语音报警";
		item1.iconId = R.mipmap.sound_119;
		items.add(item1);

		SmartItemInfo item2 = new SmartItemInfo();
		item2.name = "位置报警";
		item2.iconId = R.mipmap.location_119;
		items.add(item2);

		SmartItemInfo item3 = new SmartItemInfo();
		item3.name = "视频报警";
		item3.iconId = R.mipmap.video_119;
		items.add(item3);

		SmartItemInfo item4 = new SmartItemInfo();
		item4.name = "照片报警";
		item4.iconId = R.mipmap.photo_119;
		items.add(item4);

		SmartItemInfo item5 = new SmartItemInfo();
		item5.name = "文字报警";
		item5.iconId = R.mipmap.word_119;
		items.add(item5);

		SmartItemInfo item6 = new SmartItemInfo();
		item6.name = "电话报警";
		item6.iconId = R.mipmap.phone_119;
		items.add(item6);

		return items;
	}

}