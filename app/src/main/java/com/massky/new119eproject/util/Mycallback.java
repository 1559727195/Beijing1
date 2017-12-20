package com.massky.new119eproject.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.massky.new119eproject.AddTogenInterface.AddTogglenInterfacer;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;


/**
 * Created by masskywcy on 2016-11-04.
 */
public class Mycallback extends StringCallback implements ApiResult {
    /**
     * 上下文对象
     *
     * @param Context context
     */
    private Context context;
    private int wrongtoken_index = 2;
    /**
     * 加载数据动画展示
     *
     * @param DialogUtil dialogUtil
     */
    private DialogUtil dialogUtil;
    private AddTogglenInterfacer addTogglenInterfacer;
    private final Activity activity;

    public Mycallback(AddTogglenInterfacer addTogglenInterfacer, Context context, DialogUtil dialogUtil) {
        this.context = context;
        activity = (Activity)context;
        this.dialogUtil = dialogUtil;
        this.addTogglenInterfacer = addTogglenInterfacer;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        LogUtil.i("这是异常error", e.getMessage() + "");
        remove();
        ToastUtil.showDelToast(context, "网络连接超时");
    }

    @Override
    public void onResponse(String response, int id) {
        LogUtil.eLength("这是返回数据", response + "");
        remove();
        if (TextUtils.isEmpty(response)) {
            emptyResult();
        } else {
            User user = new GsonBuilder().registerTypeAdapterFactory(
                    new NullStringToEmptyAdapterFactory()).create().fromJson(response, User.class);
                if (user != null) {
                    if(user.result.equals("100")) {
                        onSuccess(user);
                    } else if(user.result.equals("101")){
                        emptyResult();
                    }
                }

        }
    }

    //移除dialog动画加载
    private void remove() {
        if (dialogUtil != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialogUtil.removeDialog();
                }
            });
        }
    }

    @Override
    public void emptyResult() {

    }

    @Override
    public void threeCode() {

    }

    @Override
    public void fourCode() {

    }

    @Override
    public void fiveCode() {

    }

    @Override
    public void sixCode() {

    }

    @Override
    public void sevenCode() {
        ToastUtil.showDelToast(context, "设备不存在");
    }

    @Override
    public void defaultCode() {

    }

    @Override
    public void onSuccess(User user) {
    }

    @Override
    public void pullDataError() {

    }

    @Override
    public void wrongToken() {
        //这里是是基类，把获取错误的Token写在这里
        if (wrongtoken_index > 0)
            getToken();
    }

    @Override
    public void wrongProjectCode() {

    }

    private void getToken() {
        wrongtoken_index --;
        String encryPass = (String) SharedPreferencesUtil.getData(context, "loginPassword", "");
        String password = DES.decryptDES(encryPass, "12345678");
        String loginPhone = (String) SharedPreferencesUtil.getData(context, "loginPhone", "");
        Map<String, Object> map = new HashMap<>();
        String time = Timeuti.getTime();
        map.put("loginAccount", loginPhone);
        map.put("timeStamp", time);
        map.put("signature", MD5Util.md5(loginPhone + encryPass + time));
        LogUtil.eLength("重新传入数据", new Gson().toJson(map));
        MyOkHttp.postMapObject(ApiHelper.Jingrui_getToken, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {

            }
        }, context, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                String broexpires = (String) SharedPreferencesUtil.getData(context, "deadline", "");
                int logintime = (int) System.currentTimeMillis();
//                Intent broadcast = new Intent("com.massky.jinruicenterpark.broadcast");
//                broadcast.putExtra("deadline", broexpires);
//                broadcast.putExtra("timestamp", logintime);
//                context.sendBroadcast(broadcast);
            }

            @Override
            public void onSuccess(User user) {
                switch (user.result) {
                    case "100":
                        wrongtoken_index = 2;
                        int logintime = (int) System.currentTimeMillis();
//                        SharedPreferencesUtil.saveData(context, "deadline", user.deadline);
//                        SharedPreferencesUtil.saveData(context, "JingRuitoken", user.token);
//                        SharedPreferencesUtil.saveData(context, "tokenTime", true);
//                        SharedPreferencesUtil.saveData(context, "logintime", logintime);

//                        Intent broadcast = new Intent("com.massky.jinruicenterpark.broadcast");
//                        broadcast.putExtra("deadline", user.deadline);
//                        broadcast.putExtra("timestamp", logintime);
//                        context.sendBroadcast(broadcast);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(100);
                                    addTogglenInterfacer.addTogglenInterfacer();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        break;
                }
            }

            @Override
            public void wrongToken() {//101->wrongToken
                super.wrongToken();
                if (wrongtoken_index <= 0) {
                    //弹出退出到登录，的对话框
                    Intent broadcast = new Intent("com.massky.jinruicenterpark.broadcast.wrongtoken");
                    context.sendBroadcast(broadcast);
                }
            }
        });
    }
}
