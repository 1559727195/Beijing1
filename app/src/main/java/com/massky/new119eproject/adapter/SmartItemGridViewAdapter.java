package com.massky.new119eproject.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.massky.new119eproject.R;
import com.massky.new119eproject.model.SmartItemInfo;
import com.massky.new119eproject.util.MyDevice;

import java.util.List;

public class SmartItemGridViewAdapter extends BaseAdapter {
	private List<SmartItemInfo> _items;
	private Context _context;
	
	/**
	 * 
	 * @param context
	 * @param items
	 */
	public SmartItemGridViewAdapter(Context context , List<SmartItemInfo> items,homeItemClickListener homeItemClickListener){
		_context = context;
		_items = items;
		this.homeItemClickListener = homeItemClickListener;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return null == _items ? 0 : _items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return _items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final SmartItemInfo item = _items.get(position);
		ViewHolder holder = null;
		if(null == convertView){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(_context).inflate(R.layout.view_smart_nav_item, null);
			holder.container = (LinearLayout) convertView.findViewById(R.id.smartItemLayout);
			holder.ivItemIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
			holder.tvItemName = (TextView) convertView.findViewById(R.id.tvName);
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}
		int width = MyDevice.WIDTH / 3;
		int height = MyDevice.WIDTH / 4;
		holder.container.setLayoutParams(new LinearLayout.LayoutParams(width
				, height));//setLayoutParams 动态设置布局
		holder.ivItemIcon.setImageResource(item.iconId);
		holder.tvItemName.setText(item.name);
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				homeItemClickListener.homeitem_click(position);
			}
		});
		return convertView;
	}

	private class ViewHolder{
		public LinearLayout container;
		public ImageView ivItemIcon;
		public TextView tvItemName;
	}


	private  homeItemClickListener homeItemClickListener;
public interface homeItemClickListener {
		void  homeitem_click (int position);
}

}
