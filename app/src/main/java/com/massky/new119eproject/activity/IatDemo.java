package com.massky.new119eproject.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.util.ContactManager;
import com.iflytek.cloud.util.ContactManager.ContactListener;
import com.iflytek.sunflower.FlowerCollector;
import com.mabeijianxi.smallvideo2.SendSmallVideoActivity;
import com.massky.new119eproject.AddTogenInterface.AddTogglenInterfacer;
import com.massky.new119eproject.R;
import com.massky.new119eproject.base.BaseActivity;
import com.massky.new119eproject.speech.setting.IatSettings;
import com.massky.new119eproject.speech.util.FucUtil;
import com.massky.new119eproject.speech.util.JsonParser;
import com.massky.new119eproject.util.ApiHelper;
import com.massky.new119eproject.util.DialogUtil;
import com.massky.new119eproject.util.LogUtil;
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
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

public class IatDemo extends BaseActivity implements OnClickListener {
	private static String TAG = IatDemo.class.getSimpleName();
	// 语音听写对象
	private SpeechRecognizer mIat;
	// 语音听写UI
	private RecognizerDialog mIatDialog;
	// 用HashMap存储听写结果
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

	private EditText mResultText;
	private Toast mToast;
	private SharedPreferences mSharedPreferences;
	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;

	private boolean mTranslateEnable = false;
	@InjectView(R.id.status_view)
	StatusView mStatusView;

	@InjectView(R.id.img_talk)
	ImageView img_talk; //点击录音

	@InjectView(R.id.talk_txt)
	TextView talk_txt;
	private DialogUtil dialogUtil;

	@InjectView(R.id.quit)
	Button submit_talk;
	@InjectView(R.id.back)
	ImageView back;
	private StringBuffer stringBuffer_talk = new StringBuffer();

	/**
	 * 初始化Layout。
	 */
	private void initLayout() {
//		findViewById(R.id.iat_recognize).setOnClickListener(IatDemo.this);
////		findViewById(R.id.iat_recognize_stream).setOnClickListener(IatDemo.this);
////		findViewById(R.id.iat_upload_contacts).setOnClickListener(IatDemo.this);
////		findViewById(R.id.iat_upload_userwords).setOnClickListener(IatDemo.this);
//		findViewById(R.id.iat_stop).setOnClickListener(IatDemo.this);
//		findViewById(R.id.iat_cancel).setOnClickListener(IatDemo.this);
//		findViewById(R.id.image_iat_set).setOnClickListener(IatDemo.this);
	}

	int ret = 0; // 函数调用返回值

	@Override
	public void onClick(View view) {
		if( null == mIat ){
			// 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
			this.showTip( "创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化" );
			return;
		}
		
		switch (view.getId()) {
		// 开始听写
		// 如何判断一次听写结束：OnResult isLast=true 或者 onError
		case R.id.iat_recognize:
//			// 移动数据分析，收集开始听写事件
//			FlowerCollector.onEvent(IatDemo.this, "iat_recognize");
//
//			mResultText.setText(null);// 清空显示内容
//			mIatResults.clear();
//			// 设置参数
//			setParam();
//			ret = mIat.startListening(mRecognizerListener);
//			if (ret != ErrorCode.SUCCESS) {
//				showTip("听写失败,错误码：" + ret);
//			} else {
//				showTip(getString(R.string.text_begin));
//			}
			break;
		case R.id.iat_stop:
//			mIat.stopListening();
//			showTip("停止听写");
			break;
		default:
			break;
			case R.id.img_talk:

				break;//开始说话

			case R.id.quit://提交语音转文字文件

				if (stringBuffer_talk.toString() == null || stringBuffer_talk.toString().equals("")) {
					ToastUtil.showToast(IatDemo.this, "请先录音");
					return;
				}
				shangchuan_talk(stringBuffer_talk.toString() == null?"":stringBuffer_talk.toString().trim());
				break;
			case R.id.back:
				IatDemo.this.finish();
				break;
		}
	}

	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败，错误码：" + code);
			}
		}
	};

	/**
	 * 上传联系人/词表监听器。
	 */
	private LexiconListener mLexiconListener = new LexiconListener() {

		@Override
		public void onLexiconUpdated(String lexiconId, SpeechError error) {
			if (error != null) {
				showTip(error.toString());
			} else {
				showTip(getString(R.string.text_upload_success));
			}
		}
	};

	/**
	 * 听写监听器。
	 */
	private RecognizerListener mRecognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			// 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
//			showTip("开始说话");
		}

		@Override
		public void onError(SpeechError error) {
			// Tips：
			// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
			// 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
			if(mTranslateEnable && error.getErrorCode() == 14002) {
				showTip( error.getPlainDescription(true)+"\n请确认是否已开通翻译功能" );
			} else {
				showTip(error.getPlainDescription(true));
			}
		}

		@Override
		public void onEndOfSpeech() {
			// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
//			showTip("结束说话");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.d(TAG, results.getResultString());
			if( mTranslateEnable ){
				printTransResult( results );
			}else{
				printResult(results);
			}
			
			if (isLast) {
				// TODO 最后的结果
			}
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
//			showTip("当前正在说话，音量大小：" + volume);
			Log.d(TAG, "返回音频数据："+data.length);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			//	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			//		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			//		Log.d(TAG, "session id =" + sid);
			//	}
		}
	};

	// talk_index ;
	private int talk_index  = 0;
	private void printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mIatResults.put(sn, text);

		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}

//		mResultText.setText(resultBuffer.toString());
//		mResultText.setSelection(mResultText.length());
		//语音转换成视频的地方
		//第一次为数据，第二次为标点符号
		talk_index++;
		if (talk_index >= 2) {
			talk_index = 0;
			Log.e("robin debug", "resultBuffer.toString():" + resultBuffer.toString());
			stringBuffer_talk = new StringBuffer();
			stringBuffer_talk.append(resultBuffer.toString());
		}
	}

	/**
	 * 上传语音视频转汉字
	 * @param talk_detail_string
	 */
	private void shangchuan_talk(String talk_detail_string) {
		String location_address = (String) SharedPreferencesUtil.getData(IatDemo.this,"location_address","");
//		SharedPreferencesUtil.saveData(getActivity(),"addressPoint",x + "," + y);
		String addressPoint = (String) SharedPreferencesUtil.getData(IatDemo.this,"addressPoint","");
		Map<String, Object> map = new HashMap<>();
		map.put("type","1");
		map.put("content",talk_detail_string);
		map.put("address",location_address);//地址必须写一个，不能为空 ,location_address
		String userName = (String) SharedPreferencesUtil.getData(IatDemo.this,"loginAccount","");
		map.put("userName",userName);
		//addressPoint
		map.put("addressPoint",addressPoint);
		LogUtil.eLength("查看数据", new Gson().toJson(map));
		MyOkHttp.postMapObject(ApiHelper.Beijing_talk, map, new Mycallback(new AddTogglenInterfacer() {
			@Override
			public void addTogglenInterfacer() {

			}
		}, IatDemo.this, dialogUtil) {
			@Override
			public void onError(Call call, Exception e, int id) {
				super.onError(call, e, id);
				ToastUtil.showDelToast(IatDemo.this, "网络连接超时");
			}

			@Override
			public void onSuccess(User user) {
					ToastUtil.showToast(IatDemo.this,"提交成功");
					IatDemo.this.finish();
			}

			@Override
			public void wrongToken() {
				super.wrongToken();
			}
		});
	}

	/**
	 * 听写UI监听器
	 */
	private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			if( mTranslateEnable ){
				printTransResult( results );
			}else{
				printResult(results);
			}
		}

		/**
		 * 识别回调错误.
		 */
		public void onError(SpeechError error) {
			if(mTranslateEnable && error.getErrorCode() == 14002) {
				showTip( error.getPlainDescription(true)+"\n请确认是否已开通翻译功能" );
			} else {
				showTip(error.getPlainDescription(true));
			}
		}

	};


	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}

	/**
	 * 参数设置
	 * 
	 * @return
	 */
	public void setParam() {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);

		// 设置听写引擎
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
		
		this.mTranslateEnable = mSharedPreferences.getBoolean( this.getString(R.string.pref_key_translate), false );
		if( mTranslateEnable ){
			Log.i( TAG, "translate enable" );
			mIat.setParameter( SpeechConstant.ASR_SCH, "1" );
			mIat.setParameter( SpeechConstant.ADD_CAP, "translate" );
			mIat.setParameter( SpeechConstant.TRS_SRC, "its" );
		}

		String lag = mSharedPreferences.getString("iat_language_preference",
				"mandarin");
		if (lag.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
			mIat.setParameter(SpeechConstant.ACCENT, null);
			
			if( mTranslateEnable ){
				mIat.setParameter( SpeechConstant.ORI_LANG, "en" );
				mIat.setParameter( SpeechConstant.TRANS_LANG, "cn" );
			}
		} else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, lag);
			
			if( mTranslateEnable ){
				mIat.setParameter( SpeechConstant.ORI_LANG, "cn" );
				mIat.setParameter( SpeechConstant.TRANS_LANG, "en" );
			}
		}

		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));
		
		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));
		
		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));
		
		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
	}
	
	private void printTransResult (RecognizerResult results) {
		String trans  = JsonParser.parseTransResult(results.getResultString(),"dst");
		String oris = JsonParser.parseTransResult(results.getResultString(),"src");

		if( TextUtils.isEmpty(trans)||TextUtils.isEmpty(oris) ){
			showTip( "解析结果失败，请确认是否已开通翻译功能。" );
		}else{
			mResultText.setText( "原始语言:\n"+oris+"\n目标语言:\n"+trans );
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if( null != mIat ){
			// 退出时释放连接
			mIat.cancel();
			mIat.destroy();
		}
	}

	@Override
	protected void onResume() {
		// 开放统计 移动数据统计分析
		FlowerCollector.onResume(IatDemo.this);
		FlowerCollector.onPageStart(TAG);
		super.onResume();
	}

	@Override
	protected void onView() {
		if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
			mStatusView.setBackgroundColor(Color.BLACK);
		}
		StatusUtils.setFullToStatusBar(this);  // StatusBar.

		initLayout();
		// 初始化识别无UI识别对象
		// 使用SpeechRecognizer对象，可根据回调消息自定义界面；
		mIat = SpeechRecognizer.createRecognizer(IatDemo.this, mInitListener);

//		// 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
//		// 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
//		mIatDialog = new RecognizerDialog(IatDemo.this, mInitListener);

		mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME,
				Activity.MODE_PRIVATE);
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		mResultText = ((EditText) findViewById(R.id.iat_text));
		dialogUtil = new DialogUtil(this);
	}

	@Override
	protected void onEvent() {
//		img_talk.setOnClickListener(this);
		img_talk.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()){
					case MotionEvent.ACTION_DOWN:
						talk_start();
						break;
					case MotionEvent.ACTION_UP:
						mIat.stopListening();
//						showTip("停止听写");
						talk_txt.setText("长按上方按钮语音");
						img_talk.setImageResource(R.mipmap.talk_on_pic);
						break;
				}
				return true;
			}
		});

		submit_talk.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	/**
	 * 开始语音
	 */
	private void talk_start() {
		img_talk.setImageResource(R.mipmap.talk_off_pic);
		talk_txt.setText("正在录音");
		// 移动数据分析，收集开始听写事件
		FlowerCollector.onEvent(IatDemo.this, "iat_recognize");

//		mResultText.setText(null);// 清空显示内容
		mIatResults.clear();
		// 设置参数
		setParam();
		ret = mIat.startListening(mRecognizerListener);
		if (ret != ErrorCode.SUCCESS) {
            showTip("听写失败,错误码：" + ret);
        } else {
            showTip(getString(R.string.text_begin));
        }
	}

	@Override
	protected void onData() {

	}

	@Override
	protected void onPause() {
		// 开放统计 移动数据统计分析
		FlowerCollector.onPageEnd(TAG);
		FlowerCollector.onPause(IatDemo.this);
		super.onPause();
	}

	@Override
	protected int viewId() {
		return R.layout.talk_act;
	}
}
