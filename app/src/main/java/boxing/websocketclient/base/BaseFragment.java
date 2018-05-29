package boxing.websocketclient.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import boxing.websocketclient.utils.UIUtils;


/**
 * Fragment 的基类,封装共用的方法
 * @author liuyuli
 */
public abstract class BaseFragment extends Fragment {
    public Context mContext;
    public BaseActivity mActivity;
    protected int page = 1;
    protected int page_total;
    protected boolean isRefresh;
    protected SwipeRefreshLayout mSwipeRefresh;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = BaseApplication.getApplication();
        mActivity = (BaseActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView(inflater, container, savedInstanceState);
        loadNetData();
        return view;
    }

    /**
     * 初始化数据
     */
    public abstract void initData();

    /**
     * 初始化view
     *
     * @return
     */
    public abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * 请求网络数据
     */
    public abstract void loadNetData();
    /**
     * 当点击到某个页面时, 刷新数据,用于必须展示最新数据的页面
     */
    public void refreshData(){};

    /**
     * 设置下拉刷新
     */
    public void setOnRefreshListener() {
        mSwipeRefresh.setProgressBackgroundColorSchemeResource(android.R.color.holo_green_light);
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_blue_light);
        mSwipeRefresh.setProgressViewOffset(false, 0, UIUtils.dip2px(50));
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
        isRefresh = false;
        if (page >= page_total) {
            baseAdapter.changeMoreStatus(BaseRecyclerAdapter.NO_MORE_DATA);
        } else {
            page++;
            loadNetData();
        }
    }

}
