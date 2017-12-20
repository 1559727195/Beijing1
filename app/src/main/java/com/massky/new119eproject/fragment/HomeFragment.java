package com.massky.new119eproject.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.service.LocationService;
import com.example.jpushdemo.ExampleUtil;
import com.example.jpushdemo.LocalBroadcastManager;
import com.example.jpushdemo.MainActivity;
import com.google.gson.Gson;
import com.iflytek.sunflower.FlowerCollector;
import com.mabeijianxi.smallvideo2.SendSmallVideoActivity;
import com.mabeijianxi.smallvideorecord2.DeviceUtils;
import com.mabeijianxi.smallvideorecord2.JianXiCamera;
import com.mabeijianxi.smallvideorecord2.MediaRecorderActivity;
import com.mabeijianxi.smallvideorecord2.model.AutoVBRMode;
import com.mabeijianxi.smallvideorecord2.model.BaseMediaBitrateConfig;
import com.mabeijianxi.smallvideorecord2.model.MediaRecorderConfig;
import com.massky.new119eproject.AddTogenInterface.AddTogglenInterfacer;
import com.massky.new119eproject.R;
import com.massky.new119eproject.activity.EditWordActivity;
import com.massky.new119eproject.activity.IatDemo;
import com.massky.new119eproject.activity.LoginActivity;
import com.massky.new119eproject.activity.PhotoSelectActivity;
import com.massky.new119eproject.activity.RegistActivity;
import com.massky.new119eproject.activity.RenZhengActivity;
import com.massky.new119eproject.activity.TongZhiActivity;
import com.massky.new119eproject.adapter.SmartItemGridViewAdapter;
import com.massky.new119eproject.base.BaseFragment1;
import com.massky.new119eproject.biz.Biz;
import com.massky.new119eproject.event.MyDialogEvent;
import com.massky.new119eproject.ftp.FTP;
import com.massky.new119eproject.model.SmartItemInfo;
import com.massky.new119eproject.permissions.RxPermissions;
import com.massky.new119eproject.util.ApiHelper;
import com.massky.new119eproject.util.BitmapUtil;
import com.massky.new119eproject.util.Constants;
import com.massky.new119eproject.util.DialogUtil;
import com.massky.new119eproject.util.LogUtil;
import com.massky.new119eproject.util.MyOkHttp;
import com.massky.new119eproject.util.Mycallback;
import com.massky.new119eproject.util.SharedPreferencesUtil;
import com.massky.new119eproject.util.ToastUtil;
import com.massky.new119eproject.util.User;
import com.massky.new119eproject.view.RoundProgressBar;
import com.massky.new119eproject.view.WrapContentGridView;
import com.massky.new119eproject.widget.ApplicationContext;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;
import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import static android.app.Activity.RESULT_OK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.massky.new119eproject.util.SharedPreferencesUtil.getData;
import static com.massky.new119eproject.util.SharedPreferencesUtil.saveData;

/**
 * Created by zhu on 2017/11/30.
 */

public class HomeFragment extends BaseFragment1 {
    @InjectView(R.id.gvSmartItems)
    WrapContentGridView _gvSmartItems;
    private List<SmartItemInfo> _smartItems;
    private SmartItemGridViewAdapter _smartItemAdapter;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    public static LocationService locationService;
    private StringBuffer sb = new StringBuffer();
    private int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 1005;

    public static final String FTP_CONNECT_SUCCESSS = "ftp连接成功";
    public static final String FTP_CONNECT_FAIL = "ftp连接失败";
    public static final String FTP_DISCONNECT_SUCCESS = "ftp断开连接";
    public static final String FTP_FILE_NOTEXISTS = "ftp上文件不存在";

    public static final String FTP_UPLOAD_SUCCESS = "ftp文件上传成功";
    public static final String FTP_UPLOAD_FAIL = "ftp文件上传失败";
    public static final String FTP_UPLOAD_LOADING = "ftp文件正在上传";

    public static final String FTP_DOWN_LOADING = "ftp文件正在下载";
    public static final String FTP_DOWN_SUCCESS = "ftp文件下载成功";
    public static final String FTP_DOWN_FAIL = "ftp文件下载失败";

    public static final String FTP_DELETEFILE_SUCCESS = "ftp文件删除成功";
    public static final String FTP_DELETEFILE_FAIL = "ftp文件删除失败";
    private List<myThread> list_ftp;
    private RoundProgressBar roundProgressBar; //ftp视频上传进度
    private Dialog dialog_ftp_video;
    private TextView ftp_txt;
    private String video_mp4_content;//VID_20171203_165618.mp4
    private String location_address;
    private DialogUtil dialogUtil;
    private View video_ftp_view;
    @InjectView(R.id.home_login)
    LinearLayout home_login;
    @InjectView(R.id.login_txt)
    TextView login_txt;
    @InjectView(R.id.tvName)
    TextView tvName;
    @InjectView(R.id.login_renzheng)
    TextView login_renzheng;

    private Timer timer;
    private TimerTask task;

    private final int SDK_PERMISSION_REQUEST = 127;
    private final int PERMISSION_REQUEST_CODE = 0x001;

    private String permissionInfo;

    private static final String[] permissionManifest = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private boolean act_exist = true;


    @Override
    protected void onData() {
        //
        list_ftp = new ArrayList<>();
        _smartItems = Biz.getSmartItems();
        if (null != _smartItems && _smartItems.size() > 0) {
            _smartItemAdapter = new SmartItemGridViewAdapter(getActivity(), _smartItems, new SmartItemGridViewAdapter.homeItemClickListener() {
                @Override
                public void homeitem_click(int position) {
                    Log.d("POSITION", "ITEM:" + position);
                    Intent intent = null;
//                    init_permissions();
                    locationService.start();// 定位SDK
                    switch (position) {
                        case 0://语音报警
                            startActivity(new Intent(getActivity(), IatDemo.class));
                            break;
                        case 1:
                            dialogUtil.loadDialog();
                            break;
                        case 2:
//                            video_luzhi();
                            go_video_ffmpeg();//用ffmpeg进行视频录制和快速压缩
                            break;
                        case 3:
                            startActivity(new Intent(getActivity(), PhotoSelectActivity.class));
                            break;
                        case 4:
                            startActivity(new Intent(getActivity(), EditWordActivity.class));
                            break;
                        case 5:
                            showCustomDialog();
                            break;
                    }
                    position_model = position;
                }
            });
            _gvSmartItems.setAdapter(_smartItemAdapter);
        }
    }

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION_HOMEFRGMENT = "com.massky.new119eproject.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION_HOMEFRGMENT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION_HOMEFRGMENT.equals(intent.getAction())) {

//                    String extras = intent.getStringExtra(JPushInterface.EXTRA_EXTRA);
                    Bundle bundle = intent.getBundleExtra(Constants.EXTRA_BUNDLE);
//                    setCostomMsg(showMsg.toString())
                    String extras =  bundle.getString(JPushInterface.EXTRA_EXTRA);

                    if (bundle != null) {
                        JSONObject jsonObject = new JSONObject(extras);
                        String type = jsonObject.getString("type");
                        switch (type) {
                            case "2":
                                String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
                                String content = bundle.getString(JPushInterface.EXTRA_ALERT);
                                if (content.equals("认证成功！")) {//认证成功,给HomeFragment发送广播，修改认证图标
                                    SharedPreferencesUtil.saveData(getActivity(), "accountType", "2");//2代表已认证
                                    init_login();//
                                }
                                break;
                            case "1":
                                Intent i = new Intent(getActivity(), TongZhiActivity.class);
                                i.putExtra(Constants.EXTRA_BUNDLE, bundle);//    launchIntent.putExtra(Constants.EXTRA_BUNDLE, args);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getActivity().startActivity(i);
                                break;
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * diao yong xi tong shexiangtou jin xing video luzhi bing baocun zai ben di wen jian jia
     */
    private void video_luzhi() {
        Intent intent_video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Uri uri = null;
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(getActivity(),
                        getActivity().getApplicationContext().getPackageName() + ".provider",
                        createMediaFile());
            } else {
               uri = Uri.fromFile(createMediaFile());
            }

//                                fileUri = Uri.fromFile(createMediaFile()); // create a file to save the video
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent_video.putExtra(MediaStore.EXTRA_OUTPUT, uri);  // set the image file name
        intent_video.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high
        // start the Video Capture Intent
        startActivityForResult(intent_video, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onEvent() {
        _gvSmartItems.setOnTouchInvalidPositionListener(new WrapContentGridView.OnTouchInvalidPositionListener() {
            @Override
            public boolean onTouchInvalidPosition(int motionEvent) {
                return true;
            }
        });
        home_login.setOnClickListener(this);
        login_renzheng.setOnClickListener(this);
        init_login();
    }

    @Override
    public void onEvent(MyDialogEvent eventData) {
        String content = (String) eventData.data;
        if (content != null) {//说明用户认证成功
            SharedPreferencesUtil.saveData(getActivity(),"accountType","2");//2代表已认证
            init_login();
        }
    }

    /**
     * 登录返回数据，账号是否登录
     */
    private void init_login() {
//        String userName = (String) SharedPreferencesUtil.getData(getActivity(),"loginAccount","");
        // saveData(LoginActivity.this,"accountType",type);
       String accountType = (String) SharedPreferencesUtil.getData(getActivity(),"accountType","");
       switch (accountType) {
           case "1":
           case "4":
               login_renzheng.setVisibility(View.VISIBLE);
               break;
           case "2"://已认证,认证用户
               login_renzheng.setVisibility(View.GONE);
               login_renzheng.setText("认证");
               break;
           case "3"://待认证
//          login_renzheng.setVisibility(View.GONE);
               login_renzheng.setVisibility(View.VISIBLE);
               login_renzheng.setText("待认证");
               break;
       }
    }

    @Override
    protected int viewId() {
        return R.layout.home_frag;
    }

    @Override
    protected void onView(View view) {
        requestPermissions();
        init_permissions();
        dialogUtil = new DialogUtil(getActivity());
        getPersimmions();
        initSmallVideo();
        permissionCheck();
        init_location();
        registerMessageReceiver();
    }

    /**
     * 初始化定位参数
     */
    private void init_location() {
        // -----------location config ------------
        locationService = ((ApplicationContext) getActivity().getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getActivity().getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }

        String userName = (String) SharedPreferencesUtil.getData(getActivity(),"loginAccount","");
//        if (!userName.equals("")) { //就每隔10min发送一次定位位置
            initTimer();
            locationService.start();// 定位SDK
//        }
    }

    /**
     * 这个是用ffmpeg做的微信小视频快录和快速压缩
     */
    public  void initSmallVideo() {
        // 设置拍摄视频缓存路径
        File dcim = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                JianXiCamera.setVideoCachePath(dcim + "/mabeijianxi/");
            } else {
                JianXiCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/",
                        "/sdcard-ext/")
                        + "/mabeijianxi/");
            }
        } else {
            JianXiCamera.setVideoCachePath(dcim + "/mabeijianxi/");
        }
        // 初始化拍摄
        JianXiCamera.initialize(false, null);
    }

    private void permissionCheck() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean permissionState = true;
            for (String permission : permissionManifest) {
                if (ContextCompat.checkSelfPermission(getActivity(), permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissionState = false;
                }
            }
            if (!permissionState) {
                ActivityCompat.requestPermissions(getActivity(), permissionManifest, PERMISSION_REQUEST_CODE);
            } else {
                setSupportCameraSize();
            }
        } else {
            setSupportCameraSize();
        }
    }

    /**
     *  这个是用ffmpeg做的微信小视频快录和快速压缩,支持照相机的尺寸
     */
    private void setSupportCameraSize() {
        Camera back = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        List<Camera.Size> backSizeList = back.getParameters().getSupportedPreviewSizes();
        StringBuilder str = new StringBuilder();
        str.append("经过检查您的摄像头，如使用后置摄像头您可以输入的高度有：");
        for (Camera.Size bSize : backSizeList) {
            str.append(bSize.height + "、");
        }
        back.release();
        Camera front = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        List<Camera.Size> frontSizeList = front.getParameters().getSupportedPreviewSizes();
        str.append("如使用前置摄像头您可以输入的高度有：");
        for (Camera.Size fSize : frontSizeList) {
            str.append(fSize.height + "、");
        }
        front.release();
//        tv_size.setText(str);
    }

    private boolean checkStrEmpty(String str, String display) {
        if (TextUtils.isEmpty(str)) {
            Toast.makeText(getActivity(), display, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     *  这个是用ffmpeg做的微信小视频快录和快速压缩,支持照相机的尺寸,去录像
     */
    public void go_video_ffmpeg() {

        String width = "360";//360
        String height = "480";//480
        String maxFramerate = "20";//20
        String bitrate = "580000";//580000
        String maxTime = "6000";//6000
        String minTime = "1500";//1500
        boolean needFull = false;//false

        BaseMediaBitrateConfig recordMode;
        BaseMediaBitrateConfig compressMode = null;


        recordMode = new AutoVBRMode();

//        if (!spinner_record.getSelectedItem().toString().equals("none")) {
//            recordMode.setVelocity(spinner_record.getSelectedItem().toString());
//        }

        if(!needFull&&checkStrEmpty(width, "请输入宽度")){
            return;
        }
        if (
                checkStrEmpty(height, "请输入高度")
                        || checkStrEmpty(maxFramerate, "请输入最高帧率")
                        || checkStrEmpty(maxTime, "请输入最大录制时间")
                        || checkStrEmpty(minTime, "请输小最大录制时间")
                        || checkStrEmpty(bitrate, "请输入比特率")
                ) {
            return;
        }
//      FFMpegUtils.captureThumbnails("/storage/emulated/0/DCIM/mabeijianxi/1496455533800/1496455533800.mp4", "/storage/emulated/0/DCIM/mabeijianxi/1496455533800/1496455533800.jpg", "1");

        MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
//                .fullScreen(needFull)
                .smallVideoWidth(needFull?0: Integer.valueOf(width))
                .smallVideoHeight(Integer.valueOf(height))
                .recordTimeMax(Integer.valueOf(maxTime))
//                .recordTimeMin(Integer.valueOf(minTime))
                .maxFrameRate(Integer.valueOf(maxFramerate))
                .videoBitrate(Integer.valueOf(bitrate))
                .captureThumbnailsTime(1)
                .build();
        MediaRecorderActivity.goSmallVideoRecorder(getActivity(), SendSmallVideoActivity.class.getName(), config);

    }


    private void requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permission = ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.LOCATION_HARDWARE, Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_SETTINGS, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_CONTACTS
                    ,Manifest.permission.CALL_PHONE}, 0x0010);
                }
            }

            if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
// 没有获得授权，申请授权
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.CALL_PHONE)) {
// 返回值：
//如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
//如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
//如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
// 弹窗需要解释为何需要该权限，再次请求授权
                    Toast.makeText(getActivity(), "请授权！", Toast.LENGTH_LONG).show();
// 帮跳转到该应用的设置界面，让用户手动授权
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getActivity().getApplicationContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
// 不需要解释为何需要该权限，直接请求授权
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CALL_PHONE},MY_PERMISSIONS_REQUEST_CALL_PHONE);
                }
            }else {
// 已经获得授权，可以打电话
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_login:
            String ss = login_txt.getText().toString();
                switch (ss) {
                    case "登录":
                        Intent openCameraIntent = new Intent(getActivity(), LoginActivity.class);
                        startActivityForResult(openCameraIntent, Constants.SCAN_REQUEST_CODE);
                        break;
                    case "退出":
                        showCustomDialog_login_out();
                        break;
                }
                break;//登录
            case R.id.login_renzheng://去认证

                String accountType = (String) SharedPreferencesUtil.getData(getActivity(),"accountType","");
                switch (accountType) {
                    case "1"://未认证
                    case "4"://认证失败
                        startActivity(new Intent(getActivity(),RenZhengActivity.class));
                        break;
                }
                break;
        }
    }



        /**
         * 登出
         */
    private void login_out () {
        String loginAccount = (String) SharedPreferencesUtil.getData(getActivity(),"loginAccount","18012345670");
        Map<String, Object> map = new HashMap<>();
        map.put("userName", loginAccount);

        LogUtil.eLength("查看数据", new Gson().toJson(map));
        MyOkHttp.postMapObject(ApiHelper.Beijing_login_out, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {

            }
        }, getActivity(), dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(getActivity(), "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                saveData(getActivity(),"loginflag",false);
                SharedPreferencesUtil.saveData(getActivity(),"loginAccount","");
                login_txt.setText("登录");
                login_renzheng.setVisibility(View.GONE);
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
                ToastUtil.showDelToast(getActivity(), "登出失败");
            }
        });
    }

    public static HomeFragment newInstance() {
        HomeFragment newFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public void onResume() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onResume(getActivity());
        FlowerCollector.onPageStart("robin");
        super.onResume();

        boolean loginflag = (boolean) getData(getActivity(), "loginflag", false);
        if (loginflag) {//登录了
            login_txt.setText("退出");
        } else {
            login_txt.setText("登录");
        }
        init_login();
    }




    @Override
    public void onPause() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onPageEnd("robin");
        FlowerCollector.onPause(getActivity());
        super.onPause();
    }

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if(getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
			/*
			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (getActivity().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }else{
                permissionsList.add(permission);
                return false;
            }

        }else{
            return true;
        }
    }



    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (Manifest.permission.CAMERA.equals(permissions[i])) {
                        setSupportCameraSize();
                    } else if (Manifest.permission.RECORD_AUDIO.equals(permissions[i])) {

                    }
                }
            }
        }
    }


    //自定义登出界面
    public void showCustomDialog_login_out() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();


        View view = LayoutInflater.from(getActivity()).inflate(R.layout.user_name_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(getActivity(),R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
              login_out();
            }
        });
    }

    //自定义电话报警弹出框
    public void showCustomDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();


        View view = LayoutInflater.from(getActivity()).inflate(R.layout.user_name_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(getActivity(),R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                CallPhone();
            }
        });
    }



    //自定义电话报位置报警弹出框
    public void showCustomDialog_location(String str_location) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.user_location_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        //tv_title
       TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
       tv_title.setText(str_location);
        //显示数据
        final Dialog dialog = new Dialog(getActivity(),R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                baojing_location();
            }
        });
    }

    //自定义视频报警弹出框
    public void showCustomDialog_video(final String str_camera_url) {
        zipVideo(str_camera_url);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.round_progress_act, null);
      final   TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        roundProgressBar = (RoundProgressBar) view.findViewById(R.id.roundProgressBar2);
        roundProgressBar.setMax(100);
        roundProgressBar.setVisibility(View.GONE);
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        ftp_txt = (TextView) view.findViewById(R.id.ftp_txt);
        video_ftp_view = (View) view.findViewById(R.id.video_ftp_view);
        //显示数据
        dialog_ftp_video = new Dialog(getActivity(),R.style.BottomDialog);
       dialog_ftp_video.setContentView(view);
       dialog_ftp_video.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog_ftp_video.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog_ftp_video.getWindow().setAttributes(p);  //设置生效
        dialog_ftp_video.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_ftp_video.dismiss();
                close_ftp_connection();//手动中途取消ftp上传
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog.dismiss();
                if (dialogUtil != null)
                    dialogUtil.loadDialog();
                video_ftp_view.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
                 ftp_send_server(str_camera_url);//ftp上传到ftp服务器 --/VID_20171203_144935.mp4
            }
        });
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
                        beijing_ftp_videos(video_mp4_content == null ? "": video_mp4_content);//上传视频文件名称后缀
                        dialog_ftp_video.dismiss();
                        ftp_txt.setText("上传视频完成");
                        break;//ftp手动中途取消上传
                    case 0:
                        ftp_txt.setText("准备上传视频，请稍后...");
                        if (dialogUtil != null)
                            dialogUtil.removeDialog();
                        break;//正在上传视频，请稍后...
                    case 2:
                        ftp_txt.setText("上传视频中..");
                        roundProgressBar.setVisibility(View.VISIBLE);
                        break;//ftp上传进度开始
                }
        }
    };

    /**
     * 位置报警
     */
    private void baojing_location() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "2");//位置
        map.put("content", location_address.toString() == null ? "苏州市" :  location_address.toString() + "发生火灾");
        map.put("address",  location_address.toString() == null ? "苏州市" :  location_address.toString());
        String userName = (String) SharedPreferencesUtil.getData(getActivity(),"loginAccount","");
        map.put("userName",userName);
        String addressPoint = (String) SharedPreferencesUtil.getData(getActivity(),"addressPoint","");
        map.put("addressPoint",addressPoint);
        MyOkHttp.postMapObject(ApiHelper.Beijing_talk, map, new Mycallback
                (new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, getActivity(), null) {
            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                ToastUtil.showToast(getActivity(),"上传成功");
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }


    private void CallPhone() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        //url:统一资源定位符
        //uri:统一资源标示符（更广）
        intent.setData(Uri.parse("tel:" + "119"));
        //开启系统拨号器
        startActivity(intent);
    }


    /***
     * Stop location service
     */
    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
//        init_location();
        super.onStart();
    }


    /**
     * 初始化定时器
     */
    long  add ;
    private void initTimer() {
        if (timer == null)
            timer = new Timer();
        //3min
        //关闭clientSocket即客户端socket
        if (task == null) {
            task = new TimerTask() {

                @Override
                public void run() {
                    if (act_exist) {
                        add++;
                        Log.e("robin debug","add:" + add);
                        if (add > 60 * 10) {
                            try {
                                closeTimer();
                                initTimer();
                                locationService.start();// 定位SDK
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            closeTimer();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            };
            timer.schedule(task, 100, 1000); // 1s后执行task,经过1ms再次执行
        } else {
            task.cancel();
        }
    }

    /**
     * 关闭定时器和socket客户端
     *
     * @throws IOException
     */
    private void closeTimer() throws IOException {
        add = 0;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }


    private int position_model;//各个模块的索引位置

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
                if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                sb =  new StringBuffer(256);
                sb.append("您当前的位置是");
                location_address = location.getAddrStr();//百度地图位置
                    if(location_address == null || location_address.toString().equals("")) {
                        if (dialogUtil != null)
                            dialogUtil.removeDialog();
                        locationService.stop();// 定位SDK
                       boolean isOpen =  isOPen(getActivity());//判断GPS是否开启
                        if(!isOpen)
                        showMissingPermissionDialog();
                        return;
                    }
                  double x =  location.getLongitude();//经度
                  double y =  location.getLatitude(); //维度
                sb.append(location.getAddrStr());
                sb.append("附近，是否确认报警");
                //您当前的位置是苏州市姑苏区苏州谈家巷在苏州工投科技创业园金昌软件6幢附近，是否确认报警
                if (dialogUtil != null)
                dialogUtil.removeDialog();
                handler_location.sendEmptyMessage(position_model);
                locationService.stop();// 定位SDK
                SharedPreferencesUtil.saveData(getActivity(),"location_address",location_address);
                    SharedPreferencesUtil.saveData(getActivity(),"addressPoint",x + "," + y);
                    tvName.setText(location.getCity() + "消防宣传");

                    //到这里来 ，每隔10min微型消防站人员定位状态（10分钟发一次）
                    String userName = (String) SharedPreferencesUtil.getData(getActivity(),"loginAccount","");
                    if (!userName.equals("")) { //就每隔10min发送一次定位位置
                        uploadAddress(x, y,userName);
                    }
            }
        }
    };

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }


    /**
     * 百度上传经纬度
     * @param x
     * @param y
     * @param userName
     */
    private void uploadAddress(double x, double y, String userName) {
        Map<String, Object> map = new HashMap<>();
        map.put("userName", userName);//上传视频
        map.put("address", x + "," + y);
        MyOkHttp.postMapObject(ApiHelper.Beijing_app_uploadAddress, map, new Mycallback
                (new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, getActivity(), null) {
            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }

    /**
     * 处理各个模块的定位信息
     */
    Handler handler_location = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    position_model = 0;//清0
                    showCustomDialog_location(sb.toString());//显示定位位置
                    break;
            }
        }
    };


      /* 创建保存录制得到的视频文件
     *
             * @return
             * @throws IOException
     */
    private File createMediaFile() throws IOException {
        if (checkSDCardAvaliable()) {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MOVIES), "CameraDemo");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("", "failed to create directory");
                    return null;
                }
            }
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "VID_" + timeStamp;
            String suffix = ".mp4";
            File mediaFile = new File(mediaStorageDir + File.separator + imageFileName + suffix);
            return mediaFile;
        }
        return null;
    }

    /**
     * 检查sd卡是否可用
     *
     * @return
     */
    public  boolean checkSDCardAvaliable() {

        if (Environment.getExternalStorageState() == Environment.MEDIA_REMOVED) {
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.e("robin debug", "video-data:" + data.getData());
                String content_camera = data.getData().getPath();
                String[] elements = content_camera.split("CameraDemo");
                String[] strings = content_camera.split("CameraDemo/");
                //VID_20171203_165618.mp4
                video_mp4_content = strings[1];
                showCustomDialog_video("/storage/emulated/0/Movies/CameraDemo" + elements[1].toString().trim());
            }
        } else   if (resultCode == Activity.RESULT_OK && requestCode == Constants.SCAN_REQUEST_CODE) {
            final String result = data.getExtras().getString("result");
            if (TextUtils.isEmpty(result))//ab42bc2fa4223430774240259671db94
                return;
              if (result.equals("登录成功")) {
                  init_login();
              }
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


    private void init_permissions() {

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(getActivity());
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE
                ,Manifest.permission.CAMERA
                ,Manifest.permission.WRITE_SETTINGS,android.Manifest.permission.ACCESS_FINE_LOCATION
        ,android.Manifest.permission.ACCESS_COARSE_LOCATION
        ,Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_COARSE_LOCATION).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
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
                                num = (float) uploadSize / ((float) length * 140);
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


    @Override
    public void onDestroy() {
        close_ftp_connection();//关闭ftp连接
        act_exist = false;
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onDestroy();
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
     * 上传ftp视频
     * @param
     */
    private void beijing_ftp_videos(String str_video_mp4) {
        String location_address = (String) getData(getActivity(),"location_address","");
        Map<String, Object> map = new HashMap<>();
        map.put("type", "3");//上传视频
        map.put("content", str_video_mp4);//内容为视频本地地址-》VID_20171203_144232.mp4
        map.put("address", location_address);
        String userName = (String) SharedPreferencesUtil.getData(getActivity(),"loginAccount","");
        map.put("userName",userName);
        MyOkHttp.postMapObject(ApiHelper.Beijing_talk, map, new Mycallback
                (new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, getActivity(), null) {
            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                ToastUtil.showToast(getActivity(),"上传成功");
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }

    /**
     * 压缩视频文件
     */
    public  void   zipVideo (final String zipurl) {
//        // 录制
//        MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
//
//                .fullScreen(false)
//                .smallVideoWidth(360)
//                .smallVideoHeight(480)
//                .recordTimeMax(6000)
//                .recordTimeMin(1500)
//                .maxFrameRate(20)
//                .videoBitrate(600000)
//                .captureThumbnailsTime(1)
//                .build();
//        MediaRecorderActivity.goSmallVideoRecorder(this, SendSmallVideoActivity.class.getName(), config);
//// 选择本地视频压缩
//        LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
//        final LocalMediaConfig config = buidler
//                .setVideoPath(path)
//                .captureThumbnailsTime(1)
//                .doH264Compress(new AutoVBRMode())
//                .setFramerate(15)
//                .setScale(1.0f)
//                .build();
//        OnlyCompressOverBean onlyCompressOverBean = new LocalMediaCompress(config).startCompress();
    }


    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("定位服务");
        builder.setMessage("请允许访问我的位置信息");

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });

        builder.setPositiveButton("设置",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });

        builder.setCancelable(false);
        builder.show();
    }


    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
//        Intent intent = new Intent(
//                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
        // 转到手机设置界面，用户设置GPS
        Intent intent = new Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
    }

}
