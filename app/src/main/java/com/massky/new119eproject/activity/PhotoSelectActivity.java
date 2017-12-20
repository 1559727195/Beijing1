package com.massky.new119eproject.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mabeijianxi.smallvideo2.SendSmallVideoActivity;
import com.massky.new119eproject.AddTogenInterface.AddTogglenInterfacer;
import com.massky.new119eproject.R;
import com.massky.new119eproject.base.BaseActivity;
import com.massky.new119eproject.permissions.RxPermissions;
import com.massky.new119eproject.util.ApiHelper;
import com.massky.new119eproject.util.BitmapUtil;
import com.massky.new119eproject.util.DialogUtil;
import com.massky.new119eproject.util.FileUtils;
import com.massky.new119eproject.util.LogUtil;
import com.massky.new119eproject.util.MyOkHttp;
import com.massky.new119eproject.util.Mycallback;
import com.massky.new119eproject.util.SharedPreferencesUtil;
import com.massky.new119eproject.util.ToastUtil;
import com.massky.new119eproject.util.User;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.massky.new119eproject.activity.ShangChuanBaoJingActivity.dip2px;
import static com.massky.new119eproject.util.BitmapUtil.bitmapToString;

/**
 * Created by zhu on 2017/12/1.
 */

public class PhotoSelectActivity extends BaseActivity implements View.OnClickListener{
    @InjectView(R.id.status_view)
    StatusView mStatusView;

    @InjectView(R.id.img_select)
    ImageView img_select;

    //img_show
    @InjectView(R.id.img_show)
    ImageView img_show;
    @InjectView(R.id.img_rel)
    RelativeLayout img_rel;


    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;
    //请求截图
    private static final int REQUEST_CROP_PHOTO = 102;
    //请求访问外部存储
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;
    //请求写入外部存储
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;
    //调用照相机返回图片临时文件
    private File tempFile;
    private DialogUtil dialogUtil;

    private View v, view;
    private Button cancelbtn_id, camera_id, photoalbum, cancle_id, save_id, qxbutton_id, checkbutton_id;
    private EditText acctext_id;

    @InjectView(R.id.quit)
    Button submit_talk;
    @InjectView(R.id.back)
    ImageView back;
    private Bitmap bitMap;


    @Override
    protected int viewId() {
        return R.layout.photo_select;
    }

    @Override
    protected void onView() {
        init_permissions();
        initCameraImage();
//        dialogUtil = new DialogUtil(this);
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            mStatusView.setBackgroundColor(Color.BLACK);
        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        createCameraTempFile(savedInstanceState);
    }

    private void init_permissions() {

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE
        ,Manifest.permission.CAMERA
        ,Manifest.permission.WRITE_SETTINGS).subscribe(new Observer<Boolean>() {
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

    private void initCameraImage() {
        v = LayoutInflater.from(PhotoSelectActivity.this).inflate(R.layout.camera, null);
        cancelbtn_id = (Button) v.findViewById(R.id.cancelbtn_id);
        photoalbum = (Button) v.findViewById(R.id.photoalbum);
        camera_id = (Button) v.findViewById(R.id.camera_id);
//        acctext_id = (EditText) view.findViewById(R.id.acctext_id);
//        save_id = (Button) view.findViewById(R.id.save_id);
        dialogUtil = new DialogUtil(this, v, 1);
    }

    @Override
    protected void onEvent() {
        img_select.setOnClickListener(this);
        camera_id.setOnClickListener(this);
        photoalbum.setOnClickListener(this);
        img_rel.setOnClickListener(this);
        cancelbtn_id.setOnClickListener(this);
        submit_talk.setOnClickListener(this);
        back.setOnClickListener(this);

    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //权限判断
            case R.id.img_select:
                dialogUtil.loadViewBottomdialog();
            break;

            case R.id.camera_id:
                    //跳转到调用系统相机
                    dialogUtil.removeviewBottomDialog();
                    gotoCarema();
                break;

            case R.id.photoalbum:
                    //跳转到调用系统图库
                    dialogUtil.removeviewBottomDialog();
                    gotoPhoto();
                break;
            case R.id.img_rel:
                img_rel.setVisibility(View.GONE);
                img_select.setVisibility(View.VISIBLE);
                break;

            case R.id.quit://提交语音转文字文件
                //此处后面可以将bitMap转为二进制上传后台网络
                if(stringBase64 != null)
                updateAvatar(stringBase64);
                break;
            case R.id.back:
                PhotoSelectActivity.this.finish();
                break;
            case R.id.cancelbtn_id://取消打开照相
                dialogUtil.removeviewBottomDialog();
                break;
        }
    }

    /**
     * 打开文件
     * 当手机中没有一个app可以打开file时会抛ActivityNotFoundException
     * @param context     activity
     * @param file        File
     * @param contentType 文件类型如：文本（text/html）
     */
    public static void startActionFile(Context context, File file, String contentType) throws ActivityNotFoundException {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(getUriForFile(context, file), contentType);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 打开相机
     *
     * @param activity    Activity
     * @param file        File
     * @param requestCode result requestCode
     */
    public static void startActionCapture(Activity activity, File file, int requestCode) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(activity, file));
        activity.startActivityForResult(intent, requestCode);

    }

    private static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), "com.massky.new119eproject.fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }



    /**
     * 跳转到相册
     */
    private void gotoPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK);
    }


    /**
     * 跳转到照相机
     */
    private void gotoCarema() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(PhotoSelectActivity.this,
                getApplicationContext().getPackageName() + ".provider",
                tempFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(intent, REQUEST_CAPTURE);


//            startActionCapture(this, tempFile, REQUEST_CAPTURE);
//        }catch (ActivityNotFoundException e){
//
//        }
    }

    /**
     * 创建调用系统照相机待存储的临时文件
     *
     * @param savedInstanceState
     */
    private void createCameraTempFile(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey("tempFile")) {
            tempFile = (File) savedInstanceState.getSerializable("tempFile");
        } else {
            tempFile = new File(checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/image/"),
                    System.currentTimeMillis() + ".jpg");
        }
    }

    /**
     * 检查文件是否存在
     */
    private static String checkDirPath(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return "";
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }
    private String stringBase64;



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("tempFile", tempFile);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CAPTURE: //调用系统相机返回
                if (resultCode == RESULT_OK) {
//                    gotoClipActivity(Uri.fromFile(tempFile));
                    final Uri uri =   Uri.fromFile(tempFile);
//                    final Uri uri = intent.getData();
                    if (uri == null) {
                        return;
                    }
                    String cropImagePath = getRealFilePathFromUri(getApplicationContext(), uri);
                    //快速压缩

                    int x =  dip2px(PhotoSelectActivity.this,80);
                    int y =  dip2px(PhotoSelectActivity.this,80);
                    stringBase64 = bitmapToString(cropImagePath,x,y);
//                    String stringBase64 =imageToBase64(finalPath);
//                    shangchuanAvatar(stringBase64);
//                    bitMap = bm;
                    img_rel.setVisibility(View.VISIBLE);
                    img_select.setVisibility(View.GONE);
//                    img_show.setImageBitmap(bitMap);
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.color.color_f6)
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide.with(this)
                            .load(cropImagePath)
                            .apply(options)
                            .into(img_show);
                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
                if (resultCode == RESULT_OK) {
//                    Uri uri = intent.getData();
//                    gotoClipActivity(uri);
                    final Uri uri = intent.getData();
                    if (uri == null) {
                        return;
                    }
                    String cropImagePath = getRealFilePathFromUri(getApplicationContext(), uri);
                    //快速压缩

                    int x =  dip2px(PhotoSelectActivity.this,80);
                    int y =  dip2px(PhotoSelectActivity.this,80);
                    stringBase64 = bitmapToString(cropImagePath,x,y);
//                    String stringBase64 =imageToBase64(finalPath);
//                    shangchuanAvatar(stringBase64);
//                    bitMap = bm;
                    img_rel.setVisibility(View.VISIBLE);
                    img_select.setVisibility(View.GONE);
//                    img_show.setImageBitmap(bitMap);
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.color.color_f6)
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide.with(this)
                            .load(cropImagePath)
                            .apply(options)
                            .into(img_show);
                }
                break;
            case REQUEST_CROP_PHOTO:  //剪切图片返回
                if (resultCode == RESULT_OK) {
                    final Uri uri = intent.getData();
                    if (uri == null) {
                        return;
                    }
                    String cropImagePath = getRealFilePathFromUri(getApplicationContext(), uri);
                    bitMap = BitmapFactory.decodeFile(cropImagePath);
                    img_rel.setVisibility(View.VISIBLE);
                    img_select.setVisibility(View.GONE);
                    img_show.setImageBitmap(bitMap);
                }
                break;
        }
    }

    // 1: qq, 2: weixin
    private int type = 1;
    /**
     * 打开截图界面
     *
     * @param uri
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, ClipImageActivity.class);
        intent.putExtra("type", type);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }


    /**
     * 根据Uri返回文件绝对路径
     * 兼容了file:///开头的 和 content://开头的情况
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePathFromUri(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    //更新头像
    private void updateAvatar(final String bitMap) {
//        dialogUtil.loadDialog();
        sraum_updateAvatar(bitMap);
    }

    private void sraum_updateAvatar(final String bitMap) {
        String location_address = (String) SharedPreferencesUtil.getData(PhotoSelectActivity.this,"location_address","");
        Map<String, Object> map = new HashMap<>();
        map.put("type", "4");//照片
        Map map_baseImg = new HashMap();
        List<Map> list = new ArrayList<>();
        map_baseImg.put("baseImg", bitMap);
        list.add(map_baseImg);
        map.put("content", list);
        map.put("address", location_address);
        String userName = (String) SharedPreferencesUtil.getData(PhotoSelectActivity.this,"loginAccount","");
        map.put("userName",userName);
        String addressPoint = (String) SharedPreferencesUtil.getData(PhotoSelectActivity.this,"addressPoint","");
        map.put("addressPoint",addressPoint);
        MyOkHttp.postMapObject(ApiHelper.Beijing_talk, map, new Mycallback
                (new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        sraum_updateAvatar(bitMap);
                    }
                }, PhotoSelectActivity.this, dialogUtil) {
            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
//                SharedPreferencesUtil.saveData(PhotoSelectActivity.this, "avatar",
//                        BitmapUtil.bitmaptoString(bitMap));
                ToastUtil.showToast(PhotoSelectActivity.this,"提交成功");
                PhotoSelectActivity.this.finish();
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }

}
