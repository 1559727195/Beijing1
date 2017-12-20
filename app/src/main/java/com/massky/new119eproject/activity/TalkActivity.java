package com.massky.new119eproject.activity;

import android.graphics.Color;

import com.massky.new119eproject.R;
import com.massky.new119eproject.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import butterknife.InjectView;

/**
 * Created by zhu on 2017/12/1.
 */

public class TalkActivity extends BaseActivity{
    @InjectView(R.id.status_view)
    StatusView mStatusView;
    @Override
    protected int viewId() {
        return R.layout.talk_act;
    }

    @Override
    protected void onView() {
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            mStatusView.setBackgroundColor(Color.BLACK);
        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }
}
