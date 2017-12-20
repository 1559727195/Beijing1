package com.massky.new119eproject.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.massky.new119eproject.event.MyDialogEvent;

import butterknife.ButterKnife;

/**
 * Created by zhu on 2017/7/27.
 */

public abstract class BaseFragment1 extends Fragment implements View.OnClickListener{
    //控件是否已经初始化
    private boolean isCreateView = false;
    //是否已经加载过数据
    private boolean isLoadData = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(viewId(), null);
        ButterKnife.inject(this, rootView);
        onView(rootView);
        onEvent();
        onData();
        isCreateView = true;
        return rootView;
    }

    protected abstract void onData();


    protected abstract void onEvent();

    public abstract void onEvent(MyDialogEvent eventData);


    @Override
    public void onStart() {
        super.onStart();
    }

    protected abstract int viewId();

    protected abstract void onView(View view);

    private void initViews() {
        //初始化控件
    }

    //此方法在控件初始化前调用，所以不能在此方法中直接操作控件会出现空指针
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isCreateView) {
            lazyLoad();
        }
    }

    private void lazyLoad() {
        //如果没有加载过就加载，否则就不再加载了
        if(!isLoadData){
            //加载数据操作
            isLoadData=true;
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //第一个fragment会调用
        if (getUserVisibleHint())
            lazyLoad();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
