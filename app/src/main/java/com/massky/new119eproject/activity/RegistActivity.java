package com.massky.new119eproject.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.massky.new119eproject.AddTogenInterface.AddTogglenInterfacer;
import com.massky.new119eproject.R;
import com.massky.new119eproject.base.BaseActivity;
import com.massky.new119eproject.util.ApiHelper;
import com.massky.new119eproject.util.ClearEditText;
import com.massky.new119eproject.util.DialogUtil;
import com.massky.new119eproject.util.EyeUtil;
import com.massky.new119eproject.util.LogUtil;
import com.massky.new119eproject.util.MyOkHttp;
import com.massky.new119eproject.util.Mycallback;
import com.massky.new119eproject.util.SharedPreferencesUtil;
import com.massky.new119eproject.util.ToastUtil;
import com.massky.new119eproject.util.User;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import java.util.HashMap;
import java.util.Map;
import butterknife.InjectView;
import okhttp3.Call;


/**
 * Created by zhu on 2017/12/13.
 */

public class RegistActivity extends BaseActivity implements View.OnClickListener {
    @InjectView(R.id.status_view)
    StatusView mStatusView;
    @InjectView(R.id.edit_phone)
    ClearEditText edit_phone;
    @InjectView(R.id.btn_phone)
    Button btn_phone;
    @InjectView(R.id.line_phone)
    LinearLayout line_phone;
    @InjectView(R.id.new_pass_rel)
    RelativeLayout new_pass_rel;

    @InjectView(R.id.edit_password_new)
    ClearEditText edit_password_new;
    @InjectView(R.id.edit_password_again)
    ClearEditText edit_password_again;
    @InjectView(R.id.new_password_confirm)
    Button new_password_confirm;
    @InjectView(R.id.eyeimageview_id)
    ImageView eyeimageview_id;
    @InjectView(R.id.eyeimageview_id_again)
    ImageView eyeimageview_id_again;
    private EyeUtil eyeUtil_one;
    private EyeUtil eyeUtil_two;
    @InjectView(R.id.renzheng_confirm)
    Button renzheng_confirm;
    private String do_it;
    private DialogUtil dialogUtil;

    @Override
    protected int viewId() {
        return R.layout.regist_act;
    }

    @Override
    protected void onView() {
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            mStatusView.setBackgroundColor(Color.GRAY);
        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        dialogUtil =  new DialogUtil(this);
    }

    @Override
    protected void onEvent() {
        btn_phone.setOnClickListener(this);
        new_password_confirm.setOnClickListener(this);

        eyeimageview_id.setOnClickListener(this);
        eyeUtil_one = new EyeUtil(this, eyeimageview_id, edit_password_new, true);
        eyeimageview_id_again.setOnClickListener(this);
        eyeUtil_two = new EyeUtil(this, eyeimageview_id_again, edit_password_again, true);
        renzheng_confirm.setOnClickListener(this);


    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_phone://phone淡出，验证码页淡入
                next_step();
                break;
            case R.id.new_password_confirm:
                do_it = "去注册";
                go_on_doit();
                break;
            case R.id.renzheng_confirm://去认证
                do_it = "去认证";
                go_on_doit();
                break;
            case R.id.eyeimageview_id:
                eyeUtil_one.EyeStatus();
                break;
            case R.id.eyeimageview_id_again:
                eyeUtil_two.EyeStatus();
                break;

        }

    }

    /**
     * 下一步
     */
    private void next_step() {
        if (edit_phone.getText().toString().trim().equals("")) {
            ToastUtil.showToast(RegistActivity.this, "手机号为空");
            return;
        }

        //校验新密码
        if (edit_password_new.getText().toString().trim().equals("") &&
                edit_password_again.getText().toString().trim().equals("")
                ) {
            ToastUtil.showToast(RegistActivity.this, "新密码为空");
            return;
        } else {
            if (!edit_password_new.getText().toString().trim().equals(edit_password_again.getText().toString().trim())) {
                ToastUtil.showToast(RegistActivity.this, "两次密码输入不一致");
                edit_password_new.setText("");
                edit_password_again.setText("");
                edit_password_new.setFocusable(true);
                edit_password_new.requestFocus();

                return;
            } else {
//                        change_password();
                //确认去注册
                //下一步
                line_phone.setVisibility(View.GONE);
                new_pass_rel.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * just  do it
     */
    private void go_on_doit() {
        //校验新密码
        if (edit_password_new.getText().toString().trim().equals("") &&
                edit_password_again.getText().toString().trim().equals("")
                ) {
            ToastUtil.showToast(RegistActivity.this, "新密码为空");
            return;
        } else {
            if (!edit_password_new.getText().toString().trim().equals(edit_password_again.getText().toString().trim())) {
                ToastUtil.showToast(RegistActivity.this, "两次密码输入不一致");
                edit_password_new.setText("");
                edit_password_again.setText("");
                edit_password_new.setFocusable(true);
                edit_password_new.requestFocus();

                return;
            } else {
//                        change_password();
                //确认去注册
                go_on_regist();
            }
        }
    }

    /**
     * 去注册
     */
    private void go_on_regist() {
        Map<String, Object> map = new HashMap<>();
        map.put("userName", edit_phone.getText().toString().trim());
        map.put("password", edit_password_again.getText().toString().trim());
        LogUtil.eLength("查看数据", new Gson().toJson(map));
        MyOkHttp.postMapObject(ApiHelper.Beijing_register, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
            }
        }, RegistActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(RegistActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
             SharedPreferencesUtil.saveData(RegistActivity.this,"loginAccount",edit_phone.getText().toString().trim());
                switch (do_it) {
                    case "去注册":

                        break;
                    case "去认证":
                        startActivity(new Intent(RegistActivity.this,RenZhengActivity.class));
                        break;
                }
                finish();
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }

            @Override
            public void emptyResult() {
                ToastUtil.showToast(RegistActivity.this,"号码已被注册");
            }
        });
    }
}
