package com.massky.new119eproject.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mabeijianxi.smallvideo2.SendSmallVideoActivity;
import com.massky.new119eproject.AddTogenInterface.AddTogglenInterfacer;
import com.massky.new119eproject.R;
import com.massky.new119eproject.base.BaseActivity;
import com.massky.new119eproject.util.ApiHelper;
import com.massky.new119eproject.util.BitmapUtil;
import com.massky.new119eproject.util.ClearEditText;
import com.massky.new119eproject.util.DialogUtil;
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

/**
 * Created by zhu on 2017/12/2.
 */

public class EditWordActivity extends BaseActivity implements View.OnClickListener {
    @InjectView(R.id.quit)
    Button submit_talk;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView mStatusView;
    private DialogUtil dialogUtil;

    @InjectView(R.id.login_user_name)
    ClearEditText login_user_name;

    @Override
    protected int viewId() {
        return R.layout.edit_word_act;
    }

    @Override
    protected void onView() {
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            mStatusView.setBackgroundColor(Color.BLACK);
        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.

        dialogUtil = new DialogUtil(this);
    }

    @Override
    protected void onEvent() {
        submit_talk.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quit://提交语音转文字文件
                sraum_edit_text(login_user_name.getText().toString() == "" ? "" : login_user_name.getText().toString());
                break;
            case R.id.back:
                EditWordActivity.this.finish();
                break;
        }
    }


    private void sraum_edit_text(final String edit_content) {
        String location_address = (String) SharedPreferencesUtil.getData(EditWordActivity.this,"location_address","");
        Map<String, Object> map = new HashMap<>();
        map.put("type", "5");//照片
        map.put("content", edit_content);
        map.put("address", location_address);
        String userName = (String) SharedPreferencesUtil.getData(EditWordActivity.this,"loginAccount","");
        map.put("userName",userName);
        String addressPoint = (String) SharedPreferencesUtil.getData(EditWordActivity.this,"addressPoint","");
        map.put("addressPoint",addressPoint);
        MyOkHttp.postMapObject(ApiHelper.Beijing_talk, map, new Mycallback
                (new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, EditWordActivity.this, dialogUtil) {
            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                EditWordActivity.this.finish();
                ToastUtil.showToast(EditWordActivity.this,"提交成功");
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }


}
