package com.massky.new119eproject.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.massky.new119eproject.AddTogenInterface.AddTogglenInterfacer;
import com.massky.new119eproject.MainActivity;
import com.massky.new119eproject.R;
import com.massky.new119eproject.base.BaseActivity;
import com.massky.new119eproject.permissions.RxPermissions;
import com.massky.new119eproject.util.ApiHelper;
import com.massky.new119eproject.util.ClearEditText;
import com.massky.new119eproject.util.Constants;
import com.massky.new119eproject.util.DialogUtil;
import com.massky.new119eproject.util.EyeUtil;
import com.massky.new119eproject.util.LogUtil;
import com.massky.new119eproject.util.MD5Util;
import com.massky.new119eproject.util.MyOkHttp;
import com.massky.new119eproject.util.Mycallback;
import com.massky.new119eproject.util.SharedPreferencesUtil;
import com.massky.new119eproject.util.Timeuti;
import com.massky.new119eproject.util.ToastUtil;
import com.massky.new119eproject.util.User;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import cn.jpush.android.api.JPushInterface;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;

import static com.massky.new119eproject.fragment.HomeFragment.locationService;
import static com.massky.new119eproject.util.SharedPreferencesUtil.getData;
import static com.massky.new119eproject.util.SharedPreferencesUtil.saveData;

/**
 * Created by zhu on 2017/12/4.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    @InjectView(R.id.eyeimageview_id)
    ImageView eyeimageview_id;
    private EyeUtil eyeUtil;
    @InjectView(R.id.login_password)
    ClearEditText login_password;
    @InjectView(R.id.login_user_name)
    ClearEditText login_user_name;
    @InjectView(R.id.start_btn_store)
    Button start_btn_store;
    @InjectView(R.id.status_view)
    StatusView mStatusView;
    @InjectView(R.id.login_regist)
    TextView login_regist;
    private DialogUtil dialogUtil;


    @Override
    protected int viewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onView() {
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            mStatusView.setBackgroundColor(Color.BLACK);
        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        dialogUtil = new DialogUtil(this);
    }

    @Override
    protected void onEvent() {
        eyeimageview_id.setOnClickListener(this);
        eyeUtil = new EyeUtil(this, eyeimageview_id, login_password, true);
        start_btn_store.setOnClickListener(this);
        login_regist.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.eyeimageview_id:
                eyeUtil.EyeStatus();
                break;
            case R.id.start_btn_store:
                init_jpush();
                break;
            case R.id.login_regist://注册
                init_regist ();
                break;
        }
    }

    /**
     * 初始化注册
     */
    private void init_regist() {
        startActivity(new Intent(LoginActivity.this,RegistActivity.class));
    }


    /**
     * App登录
     */
    private void init_jpush() {

        String loginAccount = login_user_name.getText().toString();
        String pwd = login_password.getText().toString();
        if (loginAccount.equals("") || pwd.equals("")) {
            ToastUtil.showDelToast(LoginActivity.this, "用户名或密码不能为空");
        } else {
            dialogUtil.loadDialog();
            String szImei = getDeviceId(LoginActivity.this);
            String rid = JPushInterface.getRegistrationID(getApplicationContext());
            if (!rid.isEmpty()) {//把极光push的ReigstId上传到服务器端，以供app接收服务器端极光数据
                init_login(rid, szImei,loginAccount,pwd);
            } else {
                Toast.makeText(this, "Get registration fail, JPush init failed!", Toast.LENGTH_SHORT).show();
                JPushInterface.stopPush(getApplicationContext());
                JPushInterface.resumePush(getApplicationContext());
                JPushInterface.init(getApplicationContext());
            }
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case REQUEST_FINE_LOCATION:break;
//        }
//    }

    /**
     * 初始化登录
     * @param
     * @param rid
     * @param szImei
     */
    private void init_login(final String rid, final String szImei,final String loginAccount,final String pwd) {
        Map<String, Object> map = new HashMap<>();
        map.put("userName", loginAccount);
        map.put("password", pwd);
        map.put("regId", rid);
        map.put("phoneId",szImei);
        LogUtil.eLength("查看数据", new Gson().toJson(map));
        MyOkHttp.postMapObject(ApiHelper.Beijing_login, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
            }
        }, LoginActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(LoginActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                saveData(LoginActivity.this,"loginflag",true);
                SharedPreferencesUtil.saveData(LoginActivity.this,"loginAccount",loginAccount);
                String type = user.account.type;//type类型，1 普通用户，2认证用户， 3待认证，4 认证未通过
                saveData(LoginActivity.this,"accountType",type);
                locationService.start();// 定位SDK
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("result", "登录成功");
                intent.putExtras(bundle);
                LoginActivity.this.setResult(RESULT_OK, intent);
                finish();
                Log.e("robin debug-1","type:" + type);
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
                ToastUtil.showDelToast(LoginActivity.this, "登录失败");
            }

            @Override
            public void emptyResult() {
                ToastUtil.showToast(LoginActivity.this,"帐号密码错误");
            }
        });
    }

    public  String getDeviceId(Context context) {
        String id;
        //android.telephony.TelephonyManager
        TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        if (mTelephony.getDeviceId() != null) {
            id = mTelephony.getDeviceId();
        } else {
            //android.provider.Settings;
            id = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return id;
    }
}
