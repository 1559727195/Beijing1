package com.massky.new119eproject.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.new119eproject.AddTogenInterface.AddTogglenInterfacer;
import com.massky.new119eproject.R;
import com.massky.new119eproject.base.BaseActivity;
import com.massky.new119eproject.util.ApiHelper;
import com.massky.new119eproject.util.ClearEditText;
import com.massky.new119eproject.util.Constants;
import com.massky.new119eproject.util.DialogUtil;
import com.massky.new119eproject.util.MyOkHttp;
import com.massky.new119eproject.util.Mycallback;
import com.massky.new119eproject.util.SharedPreferencesUtil;
import com.massky.new119eproject.util.ToastUtil;
import com.massky.new119eproject.util.User;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by zhu on 2017/12/2.
 */

public class TongZhiActivity extends BaseActivity implements View.OnClickListener {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView mStatusView;

    @InjectView(R.id.baojing_title_txt)
    TextView baojing_title_txt;
    @InjectView(R.id.baojing_time)
    TextView baojing_time;
    @InjectView(R.id.baojing_buildType)
    TextView baojing_buildType;
    @InjectView(R.id.baojing_customInfo)
    TextView baojing_customInfo;
    private String buildType;
    private String time;
    private String customInfo;


    @Override
    protected int viewId() {
        return R.layout.tongzhi_act;
    }

    @Override
    protected void onView() {
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            mStatusView.setBackgroundColor(Color.BLACK);
        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        Bundle bundle = getIntent().getBundleExtra(Constants.EXTRA_BUNDLE);
        String title = null;
        String content = null;
        if(bundle!=null){
            title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            content = bundle.getString(JPushInterface.EXTRA_ALERT);

            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);////{"type":"2"}
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(extras);
                buildType = jsonObject.getString("buildType");
                //customInfo
                time = jsonObject.getString("time");
                //customInfo
                customInfo = jsonObject.getString("customInfo");
            } catch (JSONException e) {

            }
            baojing_buildType.setText(buildType);
            baojing_time.setText(time);
            baojing_customInfo.setText(customInfo);
            baojing_title_txt.setText(title);
        }
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                TongZhiActivity.this.finish();
                break;
        }
    }


}
