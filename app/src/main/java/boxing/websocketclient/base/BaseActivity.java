package boxing.websocketclient.base;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import boxing.websocketclient.R;
import boxing.websocketclient.utils.ActControl;
import boxing.websocketclient.utils.L;
import boxing.websocketclient.utils.SharedPreUtils;
import boxing.websocketclient.utils.StatusBarUtils;
import boxing.websocketclient.utils.UIUtils;

import static boxing.websocketclient.utils.SharedPreUtils.KEY_USER_ID;
import static boxing.websocketclient.utils.SharedPreUtils.KEY_USER_NAME;
import static boxing.websocketclient.utils.SharedPreUtils.KEY_USER_PSD;
import static boxing.websocketclient.utils.SharedPreUtils.USER_INFO;


/**
 * activity的基类,封装常用的方法,新建activity时继承它
 * @author liuyuli
 */
public abstract class BaseActivity extends AppCompatActivity {
    public Context mContext;
    public BaseActivity mActivity;
    protected int page = 1;
    protected int page_total;
    protected boolean isRefresh = true;
    protected SwipeRefreshLayout mSwipeRefresh;
    protected int page_success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
//        StatusBarUtils.setTransparent(this);

        L.i(mActivity + " onCreate================");
        mContext = BaseApplication.getApplication();
        mActivity = this;
        // 保存activity弱应用 到list中
        ActControl.addActivity(this);

        restoreData(savedInstanceState);

        initData();
        initView(savedInstanceState);

        loadNetData();
    }
    /**
     * 获取当前layouty的布局ID,用于设置当前布局
     * <p>
     * 交由子类实现
     *
     * @return layout Id
     */
    protected abstract int getLayoutId();

    /**
     * 初始化数据
     */
    protected void initData(){
    }

    /**
     * 初始化view
     */
    public abstract void initView(Bundle savedInstanceState);

    /**
     * 网络访问
     */
    protected abstract void loadNetData();

    /**
     * 设置下拉刷新
     */
    public void setOnRefreshListener() {
        this.mSwipeRefresh.setProgressBackgroundColorSchemeResource(R.color.bg_67e);
        this.mSwipeRefresh.setColorSchemeResources(R.color.bg_white);
        this.mSwipeRefresh.setProgressViewOffset(false, 0, UIUtils.dip2px(50));
        this.mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                page = 1;
                loadNetData();
            }
        });
    }


    /**
     * 上拉加载更多
     *
     * @param baseAdapter
     */
    public void onLodeMore(BaseRecyclerAdapter baseAdapter) {
        if (page >= page_total) {
            baseAdapter.changeMoreStatus(BaseRecyclerAdapter.NO_MORE_DATA);
        } else {
            isRefresh = false;
            if (page < 1 + page_success) {
                page++;
                loadNetData();
            }
        }
    }

    /**
     * 返回
     * 页面左上角返回按钮
     * @param view
     */
    public void onBack(View view) {
        if (onKeyBack()) {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (onKeyBack()) {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * @return true 关闭页面; false 无响应; 默认true
     */
    public boolean onKeyBack() {
        return true;
    }

    /**
     * 判断是否登录
     *
     * @return true 是 false 否
     */
    public boolean isLogin() {
        if (TextUtils.isEmpty(getUserName())) {
            return false;
        }
        return true;
    }

    /**
     * 打开页面
     *
     * @param cls 要打开的activity
     */
    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(mActivity, cls);
        startActivity(intent);
    }

    public String getIntentData(String key) {
        Intent intent = getIntent();
        return intent.getStringExtra(key);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //0.0-1.0
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    /**
     * @return 用户名
     */
    public static String getUserName() {
        return SharedPreUtils.getString(USER_INFO, KEY_USER_NAME);
    }

    /**
     * @param userName 保存用户名
     */
    public static void setUserName(String userName) {
        SharedPreUtils.setString(USER_INFO, KEY_USER_NAME, userName);
    }

    /**
     * @return 用户密码
     */
    public static String getUserPsd() {
        return SharedPreUtils.getString(USER_INFO, KEY_USER_PSD);
    }

    /**
     * @param userPsd 保存用户密码
     */
    public static void setUserPsd(String userPsd) {
        SharedPreUtils.setString(USER_INFO, KEY_USER_PSD, userPsd);
    }

    /**
     * @return 用户 uid
     */
    public static int getUserId() {
        return SharedPreUtils.getInt(USER_INFO, KEY_USER_ID);
    }

    /**
     * @param userId 保存用户 uid
     */
    public static void setUserId(int userId) {
        SharedPreUtils.setInt(USER_INFO, KEY_USER_ID, userId);
    }

    /**
     * 保存用户信息
     * @param userId 用户id
     * @param userName 用户昵称
     * @param userPsd 用户密码
     */
    public static void setUserInfo(int userId, String userName, String userPsd){
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(USER_INFO, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putInt(KEY_USER_ID, userId);
        et.putString(KEY_USER_NAME, userName);
        et.putString(KEY_USER_PSD, userPsd);
        et.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.i(mActivity + " onDestroy================");
        ActControl.removeActivity(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        L.i(mActivity + " onRestart================");
    }

    @Override
    protected void onStart() {
        super.onStart();
        L.i(mActivity + " onStart================");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreData(savedInstanceState);

        L.i(mActivity + " onRestoreInstanceState================");
    }


    @Override
    protected void onResume() {
        super.onResume();
        L.i(mActivity + " onResume================");
    }

    @Override
    protected void onPause() {
        super.onPause();
        L.i(mActivity + " onPause================");
    }

    /**
     * @param outState 保存页面数据
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        L.i(mActivity + " onSaveInstanceState================");
    }

    @Override
    protected void onStop() {
        super.onStop();
        L.i(mActivity + " onStop================");
    }

    private void restoreData(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//        }
    }
}
