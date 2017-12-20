package com.massky.new119eproject
import android.Manifest
import android.app.AlertDialog
import android.app.PendingIntent.getActivity
import android.content.*
import android.content.Intent.getIntent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import com.massky.new119eproject.activity.TongZhiActivity
import com.massky.new119eproject.adapter.FragmentViewPagerAdapter
import com.massky.new119eproject.base.BaseActivity
import com.massky.new119eproject.fragment.HomeFragment
import com.massky.new119eproject.fragment.JianKongFragment
import com.massky.new119eproject.fragment.SetFragment
import com.massky.new119eproject.fragment.ShangBaoFragment
import com.massky.new119eproject.fragment.XunChaFragment
import com.massky.new119eproject.util.Constants
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import java.util.ArrayList
import butterknife.InjectView
import cn.jpush.android.api.JPushInterface
import com.example.jpushdemo.ExampleUtil
import com.example.jpushdemo.LocalBroadcastManager
import com.example.jpushdemo.MainActivity.*
import com.massky.new119eproject.activity.EditWordActivity
import com.massky.new119eproject.activity.IatDemo
import com.massky.new119eproject.activity.PhotoSelectActivity
import com.massky.new119eproject.event.MyDialogEvent
import de.greenrobot.event.EventBus
import org.json.JSONObject

class MainActivity : BaseActivity() {
    private var _navItemLayouts  = ArrayList<LinearLayout>(5)
    private var _navImageViews = ArrayList<ImageView>(5)
    private var _navTextViews = ArrayList<TextView>(5)
    private val _fragments = ArrayList<Fragment>()
    private var homeFragment: Fragment? = null
    private var propertyFragment: Fragment? = null
    private var webFragment: Fragment? = null
    private var bbsFragment: Fragment? = null
    private var mineFragment: Fragment? = null
    //
    private val _navSelectedImages = intArrayOf(R.mipmap.firstpage_on, R.mipmap.xuncha_on, R.mipmap.jiankong_on, R.mipmap.shangbao_on, R.mipmap.set_on)

    private val _navNormalImages = intArrayOf(R.mipmap.firstpage_off, R.mipmap.xuncha_off, R.mipmap.jiankong_off, R.mipmap.shangbao_off, R.mipmap.set_off)
    private var mContentPager: ViewPager? = null

    private val houseownerPk: String? = null
    private val memberPk: String? = null
    private val nowFragment: Fragment? = null

    @InjectView(R.id.status_view)
    internal var mStatusView: StatusView? = null

    private  var eventBus:EventBus? = null
    private var eventBus_bbs:EventBus? = null

    /**
     * 需要进行检测的权限数组
     */
    protected var needPermissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)

    private val PERMISSON_REQUESTCODE = 0

    override fun viewId(): Int {
        return R.layout.content_frame
    }

    override fun onView() {
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            if(mStatusView != null
                    )
            mStatusView!!.setBackgroundColor(Color.BLACK)
        }
        StatusUtils.setFullToStatusBar(this)  // StatusBar.
        //        StatusUtils.setFullToNavigationBar(this); // NavigationBar.

        initView()
        //init_notifacation()//通知初始化
    }

    /**
     * 通知初始化
     */
    private fun init_notifacation() {
        val intent = intent
        if (null != intent) {
            val bundle = getIntent().getBundleExtra(Constants.EXTRA_BUNDLE)
            val title: String? = null//JingRuiApp
            val content: String? = null//2017-08-31 10:40:16,客厅,模块报警
            if (bundle != null) {
                //                title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
                //                content = bundle.getString(JPushInterface.EXTRA_ALERT);
                init_nofication(intent)
            }
            //            tv.setText("Title : " + title + "  " + "Content : " + content);
        }
    }

    private fun init_nofication(intent: Intent?) {
        if (null != intent) {
            val bundle = getIntent().getBundleExtra(Constants.EXTRA_BUNDLE)
            //            String title = null;//JingRuiApp
            //            String content = null;//2017-08-31 10:40:16,客厅,模块报警
            if (bundle != null) {
                //                String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);//智慧消防
                //                String content = bundle.getString(JPushInterface.EXTRA_ALERT)

                    val extras = bundle.getString(JPushInterface.EXTRA_EXTRA)////{"type":"2"}
                    val jsonObject = JSONObject(extras)
                    val type = jsonObject.getString("type")
                    when (type) {
                        "2" -> {
                            var title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE)
                            var content = bundle.getString(JPushInterface.EXTRA_ALERT)
                            if (content.equals("认证成功!")) {//认证成功,给HomeFragment发送广播，修改认证图标
                                val myEvent = MyDialogEvent()
                                myEvent.eventType = 0
                                myEvent.data = content
                                eventBus!!.post(myEvent)//发布消息
                            }
                        }
                        "1" -> {
                            val i = Intent(this@MainActivity, TongZhiActivity::class.java)
                            i.putExtra(Constants.EXTRA_BUNDLE, bundle)//    launchIntent.putExtra(Constants.EXTRA_BUNDLE, args);
                            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        }
                    }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        getNotify(intent)
        setIntent(intent)
    }

    private fun getNotify(intent: Intent) {
//        init_nofication(intent)
        // 这里不是用的commit提交，用的commitAllowingStateLoss方式。commit不允许后台执行，不然会报Deferring update until onResume 错误
        super.onNewIntent(intent)
    }

    override fun onEvent() {
        initEvent()
        refreshFragment(0)
        // 设置左上角的按钮可以点击
    }

    override fun onData() {

    }


    private fun initEvent() {
        // TODO Auto-generated method stub
        //	     mPagerAdapter = getPagerAdapter();
        val fragmentViewPagerAdapter = FragmentViewPagerAdapter(supportFragmentManager,
                mContentPager, _fragments)
        mContentPager!!.adapter = fragmentViewPagerAdapter
        mContentPager!!.offscreenPageLimit = 2//设置这句话的好处就是在viewapger下可以同时刷新3个fragment
        mContentPager!!.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {

                //ViewPager选中的项为position
                val res = resources

                for (i in _navImageViews!!.indices) {
                    if (i != position) {
                        _navImageViews!![i].setImageResource(_navNormalImages[i])//未选中项底部tab选项卡的颜色变化
                        _navTextViews!![i].setTextColor(res.getColor(R.color.text_nav_item))
                    }
                }

                _navImageViews!![position].setImageResource(_navSelectedImages[position])//选中项底部tab选项卡的颜色变化
                _navTextViews!![position].setTextColor(res.getColor(R.color.text_operation_color))

                val myEvent = MyDialogEvent()
                myEvent.eventType = position
                when (position) {
                    0//语音报警
                    ->  eventBus!!.post(myEvent)//发布消息
                    1 -> ""
                    2 ->""

                    3 -> eventBus_bbs!!.post(myEvent)//发布消息
                    4 ->""
                    5 -> ""
                }

            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}

            override fun onPageScrollStateChanged(arg0: Int) {

            }
        })
    }

    /**
     *
     */
    private fun initView() {//
        mContentPager = findViewById<View>(R.id.mViewPager) as ViewPager

        _navItemLayouts =  ArrayList()
        val navhomelayout = findViewById<View>(R.id.navHomeLayout) as LinearLayout
        _navItemLayouts.add(navhomelayout)
//        _navItemLayouts[0] = findViewById<View>(R.id.navHomeLayout) as LinearLayout
        val navPropertiesLayout = findViewById<View>(R.id.navPropertiesLayout) as LinearLayout//物业
        _navItemLayouts.add(navPropertiesLayout)

        val navWebLayout = findViewById<View>(R.id.navWebLayout) as LinearLayout
        _navItemLayouts.add(navWebLayout)

        val navBBSLayout = findViewById<View>(R.id.navBBSLayout) as LinearLayout
        _navItemLayouts.add(navBBSLayout)

        val navMineLayout = findViewById<View>(R.id.navMineLayout) as LinearLayout
        _navItemLayouts.add(navMineLayout)

        //
        for (i in _navItemLayouts!!.indices) {
            _navItemLayouts!![i].setOnClickListener(LinearLayoutOnClickListener())
        }

        _navImageViews =  ArrayList()

        var ivNavHome = findViewById<View>(R.id.ivNavHome) as ImageView
        _navImageViews.add(ivNavHome)

        var vNavProperties = findViewById<View>(R.id.ivNavProperties) as ImageView//物业ivNavProperties
        _navImageViews.add(vNavProperties)

        var ivNavWeb = findViewById<View>(R.id.ivNavWeb) as ImageView
        _navImageViews.add(ivNavWeb)

        var ivNavBBS = findViewById<View>(R.id.ivNavBBS) as ImageView
        _navImageViews.add(ivNavBBS)

        var ivNavMine = findViewById<View>(R.id.ivNavMine) as ImageView
        _navImageViews.add(ivNavMine)

        //
        _navTextViews =  ArrayList()
        var tvNavHome = findViewById<View>(R.id.tvNavHome) as TextView
        var tvNavProperties = findViewById<View>(R.id.tvNavProperties) as TextView
        var tvNavWeb = findViewById<View>(R.id.tvNavWeb) as TextView
        var tvNavBBS = findViewById<View>(R.id.tvNavBBS) as TextView
        var tvNavMine = findViewById<View>(R.id.tvNavMine) as TextView
        _navTextViews.add(tvNavHome)
        _navTextViews.add(tvNavProperties)
        _navTextViews.add(tvNavWeb)
        _navTextViews.add(tvNavBBS)
        _navTextViews.add(tvNavMine)

        //
        homeFragment = HomeFragment.newInstance()
        propertyFragment = XunChaFragment.newInstance()//new PropertyFragment () 物业
        webFragment = JianKongFragment.newInstance()
        bbsFragment = ShangBaoFragment.newInstance()
        mineFragment = SetFragment.newInstance()
        _fragments.add((homeFragment as HomeFragment?)!!)
        _fragments.add((propertyFragment as XunChaFragment?)!!)
        _fragments.add((webFragment as JianKongFragment?)!!)
        _fragments.add((bbsFragment as ShangBaoFragment?)!!)
        _fragments.add((mineFragment as SetFragment?)!!)
        eventBus = EventBus()
        eventBus!!.register(homeFragment)//注册订阅者 MyFragment
        eventBus_bbs = EventBus()
        eventBus_bbs!!.register(bbsFragment)

    }

    /**
     *
     * @param selectedIndex
     */
    private fun refreshFragment(selectedIndex: Int) {
        /**
         * 取消动画效果，消除跨页动画（比如0到3会经过1，2页面） viewPager.setCurrentItem(index,false);
         * 具备动画效果  viewPager.setCurrentItem(index);
         * @param view
         */
        mContentPager!!.setCurrentItem(selectedIndex, false)
    }

    /**
     *
     * @author Administrator
     */
    private inner class LinearLayoutOnClickListener : View.OnClickListener {

        override fun onClick(v: View) {
            val res = resources
            var index = 0
            for (i in _navImageViews!!.indices) {
                if (v.id == _navItemLayouts!![i].id) {
                    index = i
                    _navImageViews!![i].setImageResource(_navSelectedImages[i])
                    _navTextViews!![i].setTextColor(res.getColor(R.color.text_operation_color))
                } else {
                    _navImageViews!![i].setImageResource(_navNormalImages[i])
                    _navTextViews!![i].setTextColor(res.getColor(R.color.text_nav_item))
                }
            }
            refreshFragment(index)
        }
    }

    companion object {

        //通过数字来标识当前是哪个fragment，这样用于加载顶部的menu
        var NOW_FRAGMENT_NO = 0
    }

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private var isNeedCheck = true
    override fun onResume() {
//        if (isNeedCheck) {
//            checkPermissions(needPermissions)
//        }
        super.onResume()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSON_REQUESTCODE)   {
            if (!verifyPermissions(grantResults)) {      //没有授权
                showMissingPermissionDialog()              //显示提示信息
                isNeedCheck = false
            }
        }
    }


        /**
         * 显示提示信息
         *
         * @since 2.5.0
         */
       fun showMissingPermissionDialog() {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("定位")
            builder.setMessage("请开启定位权限")

            // 拒绝, 退出应用
            builder.setNegativeButton(R.string.cancel
            ) { dialog, which -> finish() }

            builder.setPositiveButton("设置"
            ) { dialog, which -> startAppSettings() }

            builder.setCancelable(false)

            builder.show()
        }

        /**
         * 启动应用的设置
         *
         * @since 2.5.0
         */
        fun startAppSettings() {
            val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + getPackageName())
            startActivity(intent)
        }

        /**
     * 检查权限
     *
     * @param
     * @since 2.5.0
     */
    fun checkPermissions(needPermissions: Array<String>) {
        //获取权限列表
            var needRequestPermissonList = findDeniedPermissions(needPermissions)
        if (null != needRequestPermissonList && needRequestPermissonList.size > 0) {
            //list.toarray将集合转化为数组
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toTypedArray(),
                    PERMISSON_REQUESTCODE)
        }
    }


    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
     fun findDeniedPermissions(needPermissions: Array<String>): ArrayList<String> {
        val needRequestPermissonList = ArrayList<String>()
        //for (循环变量类型 循环变量名称 : 要被遍历的对象)
        for (perm in needPermissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                needRequestPermissonList.add(perm)
            }
        }
        return needRequestPermissonList
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
     fun verifyPermissions(grantResults: IntArray): Boolean {
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        eventBus!!.unregister(homeFragment)//注册订阅者 MyFragment
        eventBus_bbs!!.unregister(bbsFragment)//取消注册takeOutFragment订阅者
    }
}
