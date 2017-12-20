package com.massky.new119eproject.widget;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.service.LocationService;
import com.baidu.mapapi.SDKInitializer;
import com.example.jpushdemo.PushSetActivity;
import com.iflytek.cloud.SpeechUtility;

import com.massky.new119eproject.R;
import com.zhy.http.okhttp.OkHttpUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

/**
 * 
 * @author Administrator
 *
 */
public class ApplicationContext extends Application  implements Application.ActivityLifecycleCallbacks{
	//
	private static final String TAG = ApplicationContext.class.getSimpleName();
	private Context context;
	public String calledAcccout;

	private List<Activity> activities = new ArrayList<>();
	//
	private static ApplicationContext _instance;
//	public static BluetoothOpration _BluetoothOpration;
	/**
	 * 当前Acitity个数
	 */
	private int activityAount = 0;


	// 开放平台申请的APP key & secret key
	public static String APP_KEY = "ccd38858cc5a459bbeedcf93a25ae6be";
	public static String API_URL = "https://open.ys7.com";
	public static String WEB_URL = "https://auth.ys7.com";
	private boolean isForeground;
	private boolean isDoflag;

	public LocationService locationService;
	public Vibrator mVibrator;

	/**
	 *
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		_instance = this;

//		EZOpenSDK.initLib(this, APP_KEY, "");//萤石平台的sdk在android6.0上报错，监控先不用它

//		_BluetoothOpration=new BluetoothOpration(this);
		//okhttp网络配置
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				//.addInterceptor(new LoggerInterceptor("TAG"))
				.connectTimeout(5000, TimeUnit.MILLISECONDS)
				.readTimeout(5000, TimeUnit.MILLISECONDS)
				//其他配置
				.build();

		OkHttpUtils.initClient(okHttpClient);


		//application生命周期
		this.registerActivityLifecycleCallbacks(this);//注册


		// 应用程序入口处调用，避免手机内存过小，杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
		// 如在Application中调用初始化，需要在Mainifest中注册该Applicaiton
		// 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
		// 参数间使用半角“,”分隔。
		// 设置你申请的应用appid,请勿在'='与appid之间添加空格及空转义符

		// 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误

		SpeechUtility.createUtility(ApplicationContext.this, "appid=" + getString(R.string.app_id));

		// 以下语句用于设置日志开关（默认开启），设置成false时关闭语音云SDK日志打印
		// Setting.setShowLog(false);

		/***
		 * 初始化定位sdk，建议在Application中创建
		 */
		locationService = new LocationService(getApplicationContext());
		mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
		SDKInitializer.initialize(getApplicationContext());

		JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
		JPushInterface.init(this);     		// 初始化 JPush
		setStyleBasic();//设置通知提示方式
//		Tiny.getInstance().init(this);
	}

	/**
	 * 设置通知提示方式 - 基础属性
	 */
	private void setStyleBasic() {
		BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
		builder.statusBarDrawable = R.drawable.ic_launcher;
		builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
		builder.notificationDefaults = Notification.DEFAULT_SOUND;  //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）
		JPushInterface.setPushNotificationBuilder(1, builder);
	}


	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i(TAG, "onTerminate");
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

	}

	@Override
	public void onActivityStarted(Activity activity) {

	}

	@Override
	public void onActivityResumed(Activity activity) {
	}

	@Override
	public void onActivityPaused(Activity activity) {
//		ToastUtils.getInstances().cancel();// activity死的时候，onActivityPaused(Activity activity)
		//ToastUtils.getInstances().cancel();
	}

	@Override
	public void onActivityStopped(Activity activity) {
		activityAount--;
		if (activityAount == 0) {
			isForeground = false;
			isDoflag = false;
			Log.e("zhu-","isForeground:" + isForeground);
			//
		}
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {

	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.i("info", "demo onLowMemory..");
//		_BluetoothOpration.disconnect();
//		_BluetoothOpration.onDestroy();
	}

	/**
	 *
	 * @return
	 */
	public static ApplicationContext getInstance(){

		return _instance;
	}

	/**
	 *
	 * @return
	 */
	public int getVersionCode(){
		int code = 1;
		try {
			code = _instance.getPackageManager().getPackageInfo("net.sxif.metalib", 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return code;
	}

	/**
	 *
	 * @param act
	 */
	public void addActivity(Activity act){
		activities.add(act);
	}

	/**
	 *
	 */
	public void removeActivity(){
		for(int i = activities.size() -1 ; i >= 0; i--){
			Activity activity = activities.get(i);
			activities.remove(activity);
			activity.finish();
		}
	}


	public void removeActivity_but_activity(Activity activity_new){
		for(int i = activities.size() -1 ; i >= 0; i--){
			Activity activity = activities.get(i);
			if (activity == activity_new){
				continue;
			}
			activities.remove(activity);
			activity.finish();
		}
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
}
