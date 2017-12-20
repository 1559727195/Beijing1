package com.massky.new119eproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.massky.new119eproject.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


/**
 * Created by masskywcy on 2017-05-16.
 */

public class BaoJingAdapter extends BaseAdapter {
    private List<Map> list = new ArrayList<>();

    private  String accountType;
    public BaoJingAdapter(Context context, List<Map> list) {
        super(context, list);
        this.list = list;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder = null;
        if (null == convertView) {
            mHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_app_work, null);
//            mHolder.discard = (ImageView) convertView.findViewById(R.id.discard);
            mHolder.zoneName = (TextView) convertView.findViewById(R.id.lblListItem);

            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        final View finalConvertView = convertView;
        if (list.size() != 0) {
            String id = (String) list.get(position).get("id");
            String alarmTime = (String) list.get(position).get("name");
            mHolder.zoneName.setText(alarmTime);
        }

        return convertView;
    }



    class ViewHolder {
        TextView zoneName;
    }

    public void setBackToFragmentListener(BackToFragmentListener backToFragmentListener) {
        this.backToFragmentListener = backToFragmentListener;
    }

    private BackToFragmentListener backToFragmentListener;

    public interface BackToFragmentListener {
        void backtofragment(int position);
        void promatFragment_list_change();
    }
}
