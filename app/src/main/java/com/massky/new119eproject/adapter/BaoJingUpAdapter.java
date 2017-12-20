package com.massky.new119eproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.massky.new119eproject.R;
import com.massky.new119eproject.activity.ShangChuanBaoJingActivity;
import com.massky.new119eproject.activity.VideoPlayActivity_New;
import com.massky.new119eproject.util.ToastUtil;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.squareup.picasso.Picasso.Priority.HIGH;


/**
 * Created by masskywcy on 2017-05-16.
 */

public class BaoJingUpAdapter extends BaseAdapter {
    private List<Map> list = new ArrayList<>();

    private  String accountType;
    public BaoJingUpAdapter(Context context, List<Map> list) {
        super(context, list);
        this.list = list;
    }

    //返回当前布局的样式type
    @Override
    public int getItemViewType(int position) {
        return (int) list.get(position).get("type");
    }

    //返回你有多少个不同的布局
    @Override
    public int getViewTypeCount() {
        return 3;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        ViewHolder mHolder = null;
        int Type = getItemViewType(position);
       ViewHolderContentType viewHolderContentType = null;
        ViewHolderPicType viewHolderPicType = null;
       ViewHolderVideoType viewHolderVideoType = null;
        if (null == convertView) {
            switch (Type) {
                case 0:
                    viewHolderContentType = new ViewHolderContentType();
                    convertView = LayoutInflater.from(context).inflate(R.layout.baojing_up_item, null);
//            mHolder.discard = (ImageView) convertView.findViewById(R.id.discard);
                viewHolderContentType.baojing_content_content = (TextView) convertView.findViewById(R.id.baojing_content_content);
                viewHolderContentType.baojing_time_content = (TextView) convertView.findViewById(R.id.baojing_time_content);
                viewHolderContentType.baojing_local_content = (TextView) convertView.findViewById(R.id.baojing_local_content);
                    convertView.setTag(viewHolderContentType);
                    break;
                case 1:
                   viewHolderPicType = new ViewHolderPicType();
                    convertView = LayoutInflater.from(context).inflate(R.layout.baojing_up_item_pic, null);
//            mHolder.discard = (ImageView) convertView.findViewById(R.id.discard);
                      viewHolderPicType.baojing_local_pic = (TextView) convertView.findViewById(R.id.baojing_local_pic);
                      viewHolderPicType.baojing_time_pic = (TextView) convertView.findViewById(R.id.baojing_time_pic);
                      viewHolderPicType.img_show_pic = (ImageView) convertView.findViewById(R.id.img_show_pic);
                    convertView.setTag(viewHolderPicType);
                    break;
                case 2:
                    viewHolderVideoType = new ViewHolderVideoType();
                    convertView = LayoutInflater.from(context).inflate(R.layout.baojing_up_item_video, null);
//            mHolder.discard = (ImageView) convertView.findViewById(R.id.discard);
                   viewHolderVideoType.baojing_local_video = (TextView) convertView.findViewById(R.id.baojing_local_video);
                   viewHolderVideoType.baojing_time_video= (TextView) convertView.findViewById(R.id.baojing_time_video);
//                   viewHolderVideoType.baojing_video_video = (com.devbrackets.android.exomedia.ui.widget.VideoView) convertView.findViewById(R.id.baojing_video_video);
//                   //video_play
                    viewHolderVideoType.video_play = (ImageView)  convertView.findViewById(R.id.video_play);
                    convertView.setTag(viewHolderPicType);
                    break;
            }

        } else {
            switch (Type) {
                case 0:
                    viewHolderContentType = (ViewHolderContentType) convertView.getTag();

                    break;
                case 1:
                    viewHolderPicType = (ViewHolderPicType) convertView.getTag();
                    break;
                case 2:
                    viewHolderVideoType = (ViewHolderVideoType) convertView.getTag();
                    break;
            }
        }

        switch (Type) {
            case 0:
                if (list.size() != 0) {
                    String content = (String) list.get(position).get("content");
                    String address = (String) list.get(position).get("address");
                    String time = (String) list.get(position).get("time");
                  viewHolderContentType.baojing_content_content.setText(content);
                  viewHolderContentType.baojing_time_content.setText(time);
                  viewHolderContentType.baojing_local_content.setText(address);
                }
                break;
            case 1:
                if (list.size() != 0) {
                    String content = (String) list.get(position).get("content");
                    String address = (String) list.get(position).get("address");
                    String time = (String) list.get(position).get("time");
                    viewHolderPicType.baojing_local_pic.setText(address);//image
                    viewHolderPicType.baojing_time_pic.setText(time);
                    //
//                    viewHolderPicType.img_show_pic.setText(content);
//                    Picasso.with(context).load(content)
//                            .priority(HIGH)
//                            .into(viewHolderPicType.img_show_pic);
                    Glide.with(context).load(content).into(viewHolderPicType.img_show_pic);
                    //http://img.my.csdn.net/uploads/201407/26/1406383243_5120.jpg
                    //http://api.massky.com:8080/AppFire/n3wm998j376a392v.jpg
                }
                break;
            case 2:
                if (list.size() != 0) {
                    if (viewHolderVideoType != null) {
                        String content = (String) list.get(position).get("content");
                        String address = (String) list.get(position).get("address");
                        String time = (String) list.get(position).get("time");
                        viewHolderVideoType.baojing_local_video.setText(address);
                        viewHolderVideoType.baojing_time_video.setText(time);
//                    viewHolderVideoType.baojing_video_video.setVideoURI("");//播放的URL
                       final Uri uri = Uri.parse(content);
                        //播放完成回调
                        final ViewHolderVideoType finalViewHolderVideoType = viewHolderVideoType;
                        viewHolderVideoType.video_play.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //开始播放视频
                                Intent intent = new Intent(context,VideoPlayActivity_New.class);
                                intent.putExtra("url", uri.toString());

                                 context.startActivity(intent);
                            }
                        });
                    }
                }
                break;
        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShangChuanBaoJingActivity.class);
                intent.putExtra("id", (Serializable) list.get(position).get("id").toString());
                context.startActivity(intent);
            }
        });
        return convertView;
    }


    class ViewHolderContentType {
        TextView baojing_local_content;//baojing_time_pic
        TextView baojing_time_content;//,baojing_time_pic
        TextView baojing_content_content;
    }
    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {

        }
    }


    class ViewHolderPicType {
        TextView baojing_local_pic;//baojing_time_pic
        TextView baojing_time_pic;//,baojing_time_pic
        ImageView  img_show_pic;
    }

    class ViewHolderVideoType {
        TextView baojing_local_video;//baojing_time_pic
        TextView baojing_time_video;//,baojing_time_pic
//        com.devbrackets.android.exomedia.ui.widget.VideoView baojing_video_video;
        ImageView video_play;
    }
}
