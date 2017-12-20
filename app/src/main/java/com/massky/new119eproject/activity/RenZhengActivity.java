package com.massky.new119eproject.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.massky.new119eproject.AddTogenInterface.AddTogglenInterfacer;
import com.massky.new119eproject.R;
import com.massky.new119eproject.adapter.BaoJingAdapter;
import com.massky.new119eproject.base.BaseActivity;
import com.massky.new119eproject.util.ApiHelper;
import com.massky.new119eproject.util.ClearEditText;
import com.massky.new119eproject.util.DialogUtil;
import com.massky.new119eproject.util.LogUtil;
import com.massky.new119eproject.util.MyOkHttp;
import com.massky.new119eproject.util.Mycallback;
import com.massky.new119eproject.util.SharedPreferencesUtil;
import com.massky.new119eproject.util.ToastUtil;
import com.massky.new119eproject.util.User;
import com.massky.new119eproject.view.LazyScrollView;
import com.massky.new119eproject.view.ListViewForScrollView;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by zhu on 2017/12/13.
 */

public class RenZhengActivity extends BaseActivity implements View.OnClickListener{
    @InjectView(R.id.rel_work)
    RelativeLayout rel_work;
    @InjectView(R.id.work_xiao_list)
    ListViewForScrollView work_xiao_list;
    @InjectView(R.id.rel_weixin)
    RelativeLayout rel_weixin;//weixin_xiao_list
    @InjectView(R.id.weixin_xiao_list)
    ListViewForScrollView weixin_xiao_list;
    private DialogUtil dialogUtil;
    @InjectView(R.id.scroll)
    LazyScrollView scroll;
    public String work = "close" ;
    @InjectView(R.id.expand_colapse)
    ImageView toggle_weixin;

    @InjectView(R.id.expand_colapse_work)
    ImageView toggle_work;
    private String weixing = "close";
    private List<Map> list_weixin = new ArrayList<>();
    private List<Map> list_work = new ArrayList<>();
    private String work_item_name;
    private String weixin_item_name;
    @InjectView(R.id.edit_xiaofang)
    EditText edit_xiaofang;
    @InjectView(R.id.edit_work)
    EditText edit_work;

    @InjectView(R.id.edit_phone)
    ClearEditText edit_phone;

    @InjectView(R.id.edit_weixin)
    ClearEditText edit_weixin;

    @InjectView(R.id.edit_qq)
    ClearEditText edit_qq;
    //edit_phone,edit_weixin,edit_qq,renzheng_confirm,edit_name
    @InjectView(R.id.edit_name)
    ClearEditText edit_name;

    @InjectView(R.id.renzheng_confirm)
    Button renzheng_confirm;
    @InjectView(R.id.status_view)
    StatusView mStatusView;
    private String zhiweiId;
    private String weixingxiaofangzhanId;

    @Override
    protected int viewId() {
        return R.layout.ren_zheng_act;
    }

    @Override
    protected void onView() {
        dialogUtil = new DialogUtil(this);
//        sv = (ScrollView) findViewById(R.id.act_solution_4_sv);
        scroll.smoothScrollTo(0, 0);
        scroll.setOnScrollListener(new LazyScrollView.OnScrollListener() {
            @Override
            public void onScrollChanged() {//scrollview把relativelayout的点击事件给吸收了，把系统键盘去掉
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            mStatusView.setBackgroundColor(Color.GRAY);
        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
    }

    @Override
    protected void onEvent() {
        rel_weixin.setOnClickListener(this);
        rel_work.setOnClickListener(this);
        renzheng_confirm.setOnClickListener(this);
    }

    @Override
    protected void onData() {
        BaoJingAdapter baoJingAdapter = new BaoJingAdapter(RenZhengActivity.this,new ArrayList<Map>());
        weixin_xiao_list.setAdapter(baoJingAdapter);
        work_xiao_list.setAdapter(baoJingAdapter);
        weixin_xiao_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                weixing = "close";
//                toggle_weixin.setChecked(false);
                toggle_weixin.setBackground(getResources().getDrawable(R.drawable.ic_arrowdown));
                weixin_xiao_list.setVisibility(View.GONE);
                weixin_item_name = (String) list_weixin.get(position).get("name");
                edit_xiaofang.setText(weixin_item_name);
                weixingxiaofangzhanId = (String) list_weixin.get(position).get("id");
            }
        });

        work_xiao_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                work = "close";
//                toggle_work.setChecked(false);
                toggle_work.setBackground(getResources().getDrawable(R.drawable.ic_arrowdown));
                work_xiao_list.setVisibility(View.GONE);
                work_item_name = (String) list_work.get(position).get("name");
                edit_work.setText(work_item_name);
                zhiweiId = (String) list_work.get(position).get("id");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_weixin://调微型消防站接口

                switch (weixing) {
                    case "open" :
                        weixing = "close";
                        toggle_weixin.setBackground(getResources().getDrawable(R.drawable.ic_arrowdown));
                       weixin_xiao_list.setVisibility(View.GONE);
                        break;
                    case "close":
                        go_on_weixin();
                        break;
                }
                break;
            case R.id.rel_work://调微型消防站职位接口
                switch (work) {
                    case "open" :
                        work = "close";
                        toggle_work.setBackground(getResources().getDrawable(R.drawable.ic_arrowdown));
                        work_xiao_list.setVisibility(View.GONE);
                        break;
                    case "close":
                        go_on_work();
                        break;
                }
                break;
            case R.id.renzheng_confirm:
                if (edit_qq.getText().toString().trim().equals("")) {
                    ToastUtil.showToast(RenZhengActivity.this,"qq号为空");
                    return;
                }//edit_name


                if (edit_name.getText().toString().trim().equals("")) {
                    ToastUtil.showToast(RenZhengActivity.this,"姓名为空");
                    return;
                }//edit_name

                if (edit_weixin.getText().toString().trim().equals("")) {
                    ToastUtil.showToast(RenZhengActivity.this,"微信号为空");
                    return;
                }

                if (edit_phone.getText().toString().trim().equals("")) {
                    ToastUtil.showToast(RenZhengActivity.this,"手机号为空");
                    return;
                }

                if (edit_xiaofang.getText().toString().trim().equals("")) {
                    ToastUtil.showToast(RenZhengActivity.this,"请选择消防站点");
                    return;
                }

                if (edit_work.getText().toString().trim().equals("")) {
                    ToastUtil.showToast(RenZhengActivity.this,"请选择职位");
                    return;
                }

                goto_renzheng();
                break;//开始认证
        }
    }

    /**
     * 去认证
     */
    private void goto_renzheng() {
        Map map = new HashMap();
        String userName = (String) SharedPreferencesUtil.getData(RenZhengActivity.this,"loginAccount","");
        map.put("userName",userName);
        Map map_auto = new HashMap();
        map_auto.put("qq",edit_qq.getText().toString().trim());
        // realName
        map_auto.put("realName",edit_name.getText().toString().trim());
        map_auto.put("weixin",edit_weixin.getText().toString().trim());
        map_auto.put("phoneNumber",edit_phone.getText().toString().trim());
        map_auto.put("wxxfz",weixingxiaofangzhanId);
        map_auto.put("zhiwei",zhiweiId);
        map.put("authentication",map_auto);
        MyOkHttp.postMapObject(ApiHelper.Beijing_app_authentication, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
            }
        }, RenZhengActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(RenZhengActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
                //认证成功
                SharedPreferencesUtil.saveData(RenZhengActivity.this,"accountType","3");//2代表已认证,3为待认证
                RenZhengActivity.this.finish();
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }

    /**
     * 去
     */
    private void go_on_weixin() {

        MyOkHttp.postMapObject(ApiHelper.Beijing_app_wxxfzList, new HashMap<String, Object>(), new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
            }
        }, RenZhengActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(RenZhengActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
                list_weixin = new ArrayList<>();
                for (int i = 0 ; i < user.wxxfzList.size();i++) {
                    HashMap map = new HashMap();
                    map.put("id",user.wxxfzList.get(i).id);
                    map.put("name",user.wxxfzList.get(i).name);
                    list_weixin.add(map);
                }
        /*        for (int i = 0 ; i < 30;i++) {
                    HashMap map = new HashMap();
                    map.put("id","1");
                    map.put("name","nihao" + i);
                    list.add(map);
                }*/

                BaoJingAdapter baoJingAdapter = new BaoJingAdapter(RenZhengActivity.this,list_weixin);
                weixin_xiao_list.setAdapter(baoJingAdapter);
                weixin_xiao_list.setVisibility(View.VISIBLE);
                weixing = "open";
//                toggle_weixin.setChecked(true);
                toggle_weixin.setBackground(getResources().getDrawable(R.drawable.ic_arrowup));
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }

    /**
     * 去
     */
    private void go_on_work() {

        MyOkHttp.postMapObject(ApiHelper.Beijing_app_zhiwei, new HashMap<String, Object>(), new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
            }
        }, RenZhengActivity.this, dialogUtil) {

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(RenZhengActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
                list_work = new ArrayList<>();
                for (int i = 0 ; i < user.zhiweiList.size();i++) {
                    HashMap map = new HashMap();
                    map.put("id",user.zhiweiList.get(i).id);
                    map.put("name",user.zhiweiList.get(i).name);
                    list_work.add(map);
                }

//                for (int i = 0 ; i < 30;i++) {
//                    HashMap map = new HashMap();
//                    map.put("id","1");
//                    map.put("name","nihao" + i);
//                    list.add(map);
//                }
                work = "open";
                BaoJingAdapter baoJingAdapter = new BaoJingAdapter(RenZhengActivity.this,list_work);
                work_xiao_list.setAdapter(baoJingAdapter);
                work_xiao_list.setVisibility(View.VISIBLE);
//                toggle_work.setChecked(true);
                toggle_work.setBackground(getResources().getDrawable(R.drawable.ic_arrowup));
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }
}
