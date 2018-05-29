package boxing.websocketclient.base;

import android.app.Application;
import android.os.Handler;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.concurrent.TimeUnit;

import boxing.websocketclient.BuildConfig;
import boxing.websocketclient.utils.ActControl;
import boxing.websocketclient.utils.L;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author liuyuli
 * Application
 */

public class BaseApplication extends Application {
    private static BaseApplication mApplication;

    /** 主线程ID */
    private static int mMainThreadId = -1;
    /** 主线程 */
    private static Thread mMainThread;
    /** 主线程Handler */
    private static Handler mMainThreadHandler;

    private OkHttpClient mOkHttpClient;

    // 设置cookie 持久化 自动化
//    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        mMainThreadId = android.os.Process.myTid();
        mMainThread = Thread.currentThread();
        mMainThreadHandler = new Handler();

        // 初始化日志打印器
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override public boolean isLoggable(int priority, String tag) {
                return BuildConfig.LOG_DEBUG;
            }
        });


        // 融云初始化
//        RongIMClient.init(mApplication);

        //        UIUtils.showToast(""+UIUtils.getScreenHeight() +" "+ UIUtils.getScreenWidth()+" "
        //                + UIUtils.getScreenDensity());
    }

    public static BaseApplication getApplication() {
        return mApplication;
    }

    public OkHttpClient getOkHttp() {
        if (mOkHttpClient == null) {
            // log拦截器
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            if (L.DEBUG) {
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            } else {
                interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            }

            //            那个 if 判断意思是，如果你的 token 是空的，就是还没有请求到 token，比如对于登陆请求，
            //            是没有 token 的，只有等到登陆之后才有 token，这时候就不进行附着上 token。另外，
            //            如果你的请求中已经带有验证 header 了，比如你手动设置了一个另外的 token，那么也不需要再附着这一个 token.
            //                        Interceptor mTokenInterceptor = new Interceptor() {
            //                            @Override public Response intercept(Chain chain) throws IOException {
            //                                Request originalRequest = chain.request();
            //                                if (Your.sToken == null || alreadyHasAuthorizationHeader
            // (originalRequest)) {
            //                                    return chain.proceed(originalRequest);
            //                                }
            //                                Request authorised = originalRequest.newBuilder()
            //                                        .header("Authorization", Your.sToken)
            //                                        .build();
            //                                return chain.proceed(authorised);
            //                            }
            //                        };

            //            如果你需要在遇到诸如 401 Not Authorised 的时候进行刷新 token，
            //            可以使用 Authenticator，这是一个专门设计用于当验证出现错误的时候，进行询问获取处理的拦截器：
            //            Authenticator mAuthenticator = new Authenticator() {
            //                @Override
            //                public Request authenticate(Route route, Response response) throws IOException {
            //                    Your.sToken = service.refreshToken();
            //                    return response.request().newBuilder()
            //                            .addHeader("Authorization", Your.sToken)
            //                            .build();
            //                }
            //            }

            File sdcache = getExternalCacheDir();
            int cacheSize = 10 * 1024 * 1024;
            mOkHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .retryOnConnectionFailure(true)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .cache(new Cache(sdcache.getAbsoluteFile(), cacheSize))
                    // cookie持久化
                    //                                        .cookieJar(new CookieJar() {
                    //                                            @Override
                    //                                            public void saveFromResponse(HttpUrl httpUrl,
                    // List<Cookie> list) {
                    //                                                cookieStore.put(httpUrl.host(), list);
                    //                                            }
                    //
                    //                                            @Override
                    //                                            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                    //                                                List<Cookie> cookies = cookieStore.get(httpUrl
                    // .host());
                    //                                                return cookies != null ? cookies : new
                    // ArrayList<Cookie>();
                    //                                            }
                    //                                        })
                    // 在head增加token参数
                    //                                        .addNetworkInterceptor(mTokenInterceptor)
                    // token请求失败,刷新token
                    //                                        .authenticator(mAuthenticator)
                    .build();
        }
        return mOkHttpClient;
    }


    /** 获取主线程ID */
    public static int getMainThreadId() {
        return mMainThreadId;
    }

    /** 获取boolean主线程 */
    public static Thread getMainThread() {
        return mMainThread;
    }

    /** 获取主线程的handler */
    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    public static void exit() {
        ActControl.exit();
    }
}
