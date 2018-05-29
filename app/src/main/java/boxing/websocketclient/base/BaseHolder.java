package boxing.websocketclient.base;

import android.view.View;

/**
 *
 * @author liuyuli
 * @param <Data>
 */
public abstract class BaseHolder<Data> {
    protected View mRootView;
    private Data mData;
    public BaseActivity mActivity;

    /**
     * @param activity
     */
    public BaseHolder(BaseActivity activity) {
        this.mActivity = activity;
        mRootView = initView();
        if (mRootView != null) {
            mRootView.setTag(this);
        }
    }

    public void setData(Data data) {
        this.mData = data;
        refreshView();
    }

    public Data getData() {
        return mData;
    }

    public View getRootView() {
        return mRootView;
    }

    private Object something;
    protected int position;

    public Object getSomething() {
        return something;
    }

    public void setSomething(Object something) {
        this.something = something;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public abstract View initView();

    public abstract void refreshView();
}
