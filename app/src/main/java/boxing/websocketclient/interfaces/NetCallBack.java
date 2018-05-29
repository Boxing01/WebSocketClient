package boxing.websocketclient.interfaces;

import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by liuyuli on 2017/6/6 0006.
 * 自定义 网络访问回调接口 易于扩展
 */
public interface NetCallBack {

    public abstract void onStart(String id);

    public abstract void onSuccess(JSONObject jsonObject, String id) throws Exception;

    public abstract void onFailed(Call call, String id);
}