package com.massky.new119eproject.fragment;

import android.os.Bundle;
import android.view.View;

import com.massky.new119eproject.R;
import com.massky.new119eproject.base.BaseFragment;
import com.massky.new119eproject.base.BaseFragment1;
import com.massky.new119eproject.event.MyDialogEvent;

/**
 * Created by zhu on 2017/11/30.
 */

public class SetFragment extends BaseFragment1 {
    @Override
    protected void onData() {

    }

    @Override
    protected void onEvent() {

    }

    @Override
    public void onEvent(MyDialogEvent eventData) {

    }

    @Override
    protected int viewId() {
        return R.layout.set_frag;
    }

    @Override
    protected void onView(View view) {

    }

    @Override
    public void onClick(View v) {

    }

    public static SetFragment newInstance() {
        SetFragment newFragment = new SetFragment();
        Bundle bundle = new Bundle();
        newFragment.setArguments(bundle);
        return newFragment;
    }
}
