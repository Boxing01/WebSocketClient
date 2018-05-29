package boxing.websocketclient.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * viewpager 延迟加载,
 * @author liuyuli
 */
public abstract class BaseLazyFragment extends BaseFragment {
    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible;
    private boolean isPrepared;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 可见
     */
    protected void onVisible() {
        if (!isHasLoad() || isRestartNull()) {
            lazyLoad();
        }
    }

    /**
     * 被回收了可能没有数据不加载,这时需要重写此方法
     *
     * @return
     */
    public boolean isRestartNull() {
        return false;
    }

    /**
     * 不可见
     */
    protected void onInvisible() {

    }

    /**
     * 是否加载完
     *
     * @return
     */
    public abstract boolean isHasLoad();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView(inflater, container, savedInstanceState);
        //是否初始化完成
        isPrepared = true;
        lazyLoad();
        return view;
    }

    private void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        loadNetData();
    }

    /**
     * 初始化view
     *
     * @return
     */
    @Override
    public abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * 请求数据,如果是网络数据记得调用loadNetData_get(),或loadNetData_post(); 延迟加载 子类必须重写此方法
     */
    @Override
    public abstract void loadNetData();

    /**
     * 初始化数据
     */
    @Override
    public abstract void initData();

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
