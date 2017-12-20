package com.massky.new119eproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;

import com.bumptech.glide.Glide;
import com.massky.new119eproject.AddTogenInterface.AddTogglenInterfacer;
import com.massky.new119eproject.R;
import com.massky.new119eproject.activity.PhotoSelectActivity;
import com.massky.new119eproject.activity.RenZhengActivity;
import com.massky.new119eproject.activity.ShangChuanBaoJingActivity;
import com.massky.new119eproject.adapter.BaoJingAdapter;
import com.massky.new119eproject.adapter.BaoJingUpAdapter;
import com.massky.new119eproject.base.BaseFragment;
import com.massky.new119eproject.base.BaseFragment1;
import com.massky.new119eproject.event.MyDialogEvent;
import com.massky.new119eproject.maxwin.view.XListView;
import com.massky.new119eproject.util.ApiHelper;
import com.massky.new119eproject.util.DialogUtil;
import com.massky.new119eproject.util.MyOkHttp;
import com.massky.new119eproject.util.Mycallback;
import com.massky.new119eproject.util.SharedPreferencesUtil;
import com.massky.new119eproject.util.ToastUtil;
import com.massky.new119eproject.util.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by zhu on 2017/11/30.
 */

public class ShangBaoFragment extends BaseFragment1 implements XListView.IXListViewListener{
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    private BaoJingUpAdapter baoJingAdapter;
    private DialogUtil dialogUtil;
    private List<Map> list_alarm = new ArrayList<>();
    private int currentpage = 1;
    private Handler mHandler = new Handler();

    @Override
    protected void onData() {

    }

    @Override
    protected void onEvent() {

    }

    @Override
    public void onEvent(MyDialogEvent eventData) {
        currentpage = 1;
        get_mine_alarm(currentpage,true);
    }

    @Override
    protected int viewId() {
        return R.layout.shang_bao_frag;
    }

    @Override
    protected void onView(View view) {
        list_alarm = new ArrayList<>();
        dialogUtil = new DialogUtil(getActivity());
        baoJingAdapter = new BaoJingUpAdapter(getActivity(),list_alarm);
        xListView_scan.setAdapter(baoJingAdapter);
        xListView_scan.setPullLoadEnable(true);
        xListView_scan.setXListViewListener(this);
//        xListView_scan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//               Intent intent =  new Intent(getActivity(), ShangChuanBaoJingActivity.class);
//               intent.putExtra("id", (Serializable) list_alarm.get(position).get("id").toString());
//                startActivity(intent);
//            }
//        });
        get_mine_alarm(currentpage,true);
    }

    @Override
    public void onClick(View v) {

    }

    public static ShangBaoFragment newInstance() {
        ShangBaoFragment newFragment = new ShangBaoFragment();
        Bundle bundle = new Bundle();
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public void onRefresh() {
        onLoad();
        currentpage = 1;
        get_mine_alarm(currentpage,true);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                get_mine_alarm(currentpage,false);
                onLoad();
            }
        }, 1000);
    }

    private void onLoad() {
        xListView_scan.stopRefresh();
        xListView_scan.stopLoadMore();
        xListView_scan.setRefreshTime("刚刚");
    }


    private void get_mine_alarm(final int page, final boolean isRefresh) {
        String accountType = (String) SharedPreferencesUtil.getData(getActivity(), "accountType", "");
//        if (!accountType.equals("2") || !accountType.equals("3")){//只有认证用户才能，查看报警详情
//            return;
//        }
        switch (accountType) {
            case "2":
            case "3":
                shangbao_baojing(page, isRefresh);
                break;
        }
    }

    /**
     * 下拉报警详情
     * @param page
     * @param isRefresh
     */
    private void shangbao_baojing(final int page, final boolean isRefresh) {
        final Map map = new HashMap();
        map.put("page",page);
        MyOkHttp.postMapObject(ApiHelper.Jingrui_app_fireList,map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                get_mine_alarm(page, isRefresh);
            }
        }, getActivity(), dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(getActivity(), "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
                currentpage ++;//无论是加载成功或者失败都是currentpage++;
                if(isRefresh){
                    list_alarm = new ArrayList<Map>();
                } else {

                }

                for (int i = 0; i < user.fireList.size(); i++) {
                    Map map_alarm_item = new HashMap();
                    map_alarm_item.put("id", user.fireList.get(i).id);
                    map_alarm_item.put("address", user.fireList.get(i).address);
                    //根据content.jpg ,.mp4
                    String content = user.fireList.get(i).content.toString();
                    if (content.contains(".mp4")) {
                        map_alarm_item.put("type", 2);
                    } else if (content.contains(".jpg")) {
                        map_alarm_item.put("type", 1);
                    } else {
                        map_alarm_item.put("type", 0);
                    }

                    map_alarm_item.put("content", user.fireList.get(i).content);
                    map_alarm_item.put("time", user.fireList.get(i).time);
                    list_alarm.add(map_alarm_item);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        baoJingAdapter.clear();
                        baoJingAdapter.addAll(list_alarm);
                    }
                });
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }

}
