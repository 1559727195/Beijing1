package com.mabeijianxi.smallvideo2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.mabeijianxi.smallvideorecord2.MediaRecorderActivity;
import com.massky.new119eproject.AddTogenInterface.AddTogglenInterfacer;
import com.massky.new119eproject.R;
import com.massky.new119eproject.activity.IatDemo;
import com.massky.new119eproject.activity.RenZhengActivity;
import com.massky.new119eproject.fragment.HomeFragment;
import com.massky.new119eproject.ftp.FTP;
import com.massky.new119eproject.util.ApiHelper;
import com.massky.new119eproject.util.MyOkHttp;
import com.massky.new119eproject.util.Mycallback;
import com.massky.new119eproject.util.SharedPreferencesUtil;
import com.massky.new119eproject.util.ToastUtil;
import com.massky.new119eproject.util.User;
import com.massky.new119eproject.view.RoundProgressBar;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.massky.new119eproject.fragment.HomeFragment.FTP_UPLOAD_LOADING;
import static com.massky.new119eproject.fragment.HomeFragment.FTP_UPLOAD_SUCCESS;
import static com.massky.new119eproject.util.SharedPreferencesUtil.getData;


/**
 * Created by jian on 2016/7/21 15:52
 * mabeijianxi@gmail.com
 */
public class SendSmallVideoActivity extends AppCompatActivity implements View.OnClickListener {

    private String videoUri;
    private TextView tv_send;
    private TextView tv_cancel;
    private String videoScreenshot;
    private ImageView iv_video_screenshot;
    private TextView et_send_content;

    private AlertDialog dialog;
    private RoundProgressBar roundProgressBar;
    private TextView ftp_txt;
    private View video_ftp_view;
    private TextView cancel;
    private TextView confirm;
    private List<myThread> list_ftp;
    private String up_interface_content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        tv_cancel.setOnClickListener(this);
        tv_send.setOnClickListener(this);
        et_send_content.setOnClickListener(this);
        iv_video_screenshot.setOnClickListener(this);

        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
    }


    private void initData() {
        Intent intent = getIntent();
        videoUri = intent.getStringExtra(MediaRecorderActivity.VIDEO_URI);
        videoScreenshot = intent.getStringExtra(MediaRecorderActivity.VIDEO_SCREENSHOT);
        Bitmap bitmap = BitmapFactory.decodeFile( videoScreenshot);
        iv_video_screenshot.setImageBitmap(bitmap);
        et_send_content.setHint("您视频地址为:"+videoUri);
        String[] strings = videoUri.split("mabeijianxi/");
        //storage/emulated/0/DCIM/mabeijianxi/1512700403890/1512700403890.mp4
        //VID_20171203_165618.mp4
        String [] url_content = strings[1].split("/");
        up_interface_content = url_content[1];//上传接口的视频1512700403890.mp4
        list_ftp = new ArrayList<>();
    }

    private void initView() {
        setContentView(R.layout.smallvideo_text_edit_activity);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_send = (TextView) findViewById(R.id.tv_send);
        et_send_content = (TextView) findViewById(R.id.et_send_content);
        iv_video_screenshot = (ImageView) findViewById(R.id.iv_video_screenshot);

        roundProgressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar2);
        roundProgressBar.setMax(100);
        roundProgressBar.setVisibility(View.GONE);
        cancel = (TextView) findViewById(R.id.call_cancel);
        confirm = (TextView) findViewById(R.id.call_confirm);
        ftp_txt = (TextView) findViewById(R.id.ftp_txt);
        video_ftp_view = (View) findViewById(R.id.video_ftp_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
//                hesitate();
                SendSmallVideoActivity.this.finish();
                close_ftp_connection();
                break;
            case R.id.tv_send:
                break;
            case R.id.iv_video_screenshot:
                startActivity(new Intent(this, VideoPlayerActivity.class).putExtra(
                        "path", videoUri));
                break;

            case R.id.call_confirm:
                video_ftp_view.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
                ftp_send_server(videoUri);//ftp上传到ftp服务器 --/VID_20171203_144935.mp4
                break;
            case R.id.call_cancel:
                close_ftp_connection();
//                hesitate();
                SendSmallVideoActivity.this.finish();
                break;
        }
    }
    /**
     * ftp上传到ftp服务器
     */
    private void ftp_send_server(String camera_uri) {
        close_ftp_connection();
        myThread runnable =	new myThread(camera_uri);
        Thread thread  = new Thread(runnable);
        thread.start();
        list_ftp.add(runnable);
    }


    /**
     * 关闭ftp连接
     */
    private void close_ftp_connection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list_ftp.size(); i++) {
                    try {
                        list_ftp.get(i).ftp.closeConnect();
                        list_ftp.remove(i);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }



    /**
     * ftp上传线程
     */
    private class  myThread implements Runnable {
        public FTP ftp;
        private String camera_uri;
        public  myThread(String camera_uri) {
            //单文件上传
            FTP ftp = new FTP();
            this.ftp = ftp;
            this.camera_uri = camera_uri;
        }
        @Override
        public void run() {
            // 上传
            File file = new File(camera_uri);//-/storage/emulated/0/Movies/CameraDemo/VID_20171203_140944.mp4
            ///storage/emulated/0/DCIM/mabeijianxi/1512700403890/1512700403890.mp4
            final long length = file.length();
            try {
                ftp.uploadSingleFile(file, "videoTest", new FTP.UploadProgressListener() {
                    @Override
                    public void onUploadProgress(String currentStep, long uploadSize, File file) {
                        // TODO Auto-generated method stub
                        Log.e("robin debug", currentStep);
                        if (currentStep.equals(FTP_UPLOAD_SUCCESS)) {
                            Log.e("robin debug", "-----shanchuan--successful");
                            for (int i = 0; i < list_ftp.size(); i++) {
                                if (list_ftp.get(i).ftp == ftp) {
                                    list_ftp.remove(i);
                                }
                            }
                            handler_camera_ftp.sendEmptyMessage(1);//1为视频上传完成

                        } else if (currentStep.equals(FTP_UPLOAD_LOADING)) {
//							long fize = file.length();
                            float num = 0;
                            if (Build.VERSION.SDK_INT >= 24) {
                                num = (float) uploadSize / ((float) length * 130);
                            } else {
                                num = (float) uploadSize / ((float) length * 113);
                            }
                            int result = (int) (num * 100) ;
                            Log.e("robin debug", "-----shangchuan---" + result + "%");
                            roundProgressBar.setProgress(result);
                            if (result > 0) {
                                handler_camera_ftp.sendEmptyMessage(2);//ftp上传进度开始
                            } else {
                                handler_camera_ftp.sendEmptyMessage(0);//ftp正在上传视频，请稍后...
                            }
                        }
                    }
                });
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    /**
     * 显ftp上传进度条
     */
    Handler handler_camera_ftp = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int action = msg.what;
            switch (action) {
                case 1:
                    beijing_ftp_videos(up_interface_content == null ? "": up_interface_content);//上传视频文件名称后缀
//                    dialog_ftp_video.dismiss();
                    ftp_txt.setText("上传视频完成");
                    break;//ftp手动中途取消上传
                case 0:
                    ftp_txt.setText("准备上传视频，请稍后...");
//                    if (dialogUtil != null)
//                        dialogUtil.removeDialog();
                    break;//正在上传视频，请稍后...
                case 2:
                    ftp_txt.setText("上传视频中..");
                    roundProgressBar.setVisibility(View.VISIBLE);
                    break;//ftp上传进度开始
            }
        }
    };


    /**
     * 上传ftp视频
     * @param
     */
    private void beijing_ftp_videos(String str_video_mp4) {
        String location_address = (String) getData(SendSmallVideoActivity.this,"location_address","");
        Map<String, Object> map = new HashMap<>();
        map.put("type", "3");//上传视频
        map.put("content", str_video_mp4);//内容为视频本地地址-》VID_20171203_144232.mp4
        map.put("address", location_address);
        String userName = (String) SharedPreferencesUtil.getData(SendSmallVideoActivity.this,"loginAccount","");
        map.put("userName",userName);
        String addressPoint = (String) SharedPreferencesUtil.getData(SendSmallVideoActivity.this,"addressPoint","");
        map.put("addressPoint",addressPoint);
        MyOkHttp.postMapObject(ApiHelper.Beijing_talk, map, new Mycallback
                (new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, SendSmallVideoActivity.this, null) {
            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                SendSmallVideoActivity.this.finish();
                close_ftp_connection();
                ToastUtil.showToast(SendSmallVideoActivity.this,"上传成功");
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }




    @Override
    public void onBackPressed() {
//        hesitate();
        SendSmallVideoActivity.this.finish();
    }

    private void hesitate() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.hint)
                    .setMessage(R.string.record_camera_exit_dialog_message)
                    .setNegativeButton(
                            R.string.record_camera_cancel_dialog_yes,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();

//                                    FileUtils.deleteDir(getIntent().getStringExtra(MediaRecorderActivity.OUTPUT_DIRECTORY));

                                }

                            })
                    .setPositiveButton(R.string.record_camera_cancel_dialog_no,
                            null).setCancelable(false).show();
        } else {
            dialog.show();
        }
    }

}
