package boxing.websocketclient.base;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import boxing.websocketclient.R;
import boxing.websocketclient.interfaces.ItemClickListener;
import boxing.websocketclient.utils.ActControl;
import boxing.websocketclient.utils.UIUtils;


/**
 * RecyclerView  adapter的基类
 * @author liuyuli
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerAdapter.BaseViewHolder<T>> {
    public BaseActivity mActivity = ActControl.getTop();
    private static final int HEAD_TYPE = 100;
    private static final int NORMAL_TYPE = 10;
    private static final int FOOT_TYPE = -100;
    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //到底啦
    public static final int NO_MORE_DATA = 2;

    private List<T> total = new ArrayList<>();
    private ItemClickListener listener;

    private boolean hasHeadView = false;
    private boolean hasFootView = false;
    private FootViewHolder footViewHolder;
    //设置头部数据
    private Object object;
    private RecyclerView mRecyclerView;

    public BaseRecyclerAdapter(List<T> total) {
        this.total.clear();
        this.total.addAll(total);
    }


    /**
     * 添加headView 调用此方法必须在所在adapter中实现getHeadViewHolder(View headView)方法
     */
    public void addHeadView(Object object) {
        hasHeadView = true;
        this.object = object;
    }

    /**
     * 添加上拉加载更多
     */
    public void setOnFootRefreshListener(RecyclerView rView, final LinearLayoutManager layoutManager,
                                         final OnFootRefreshListener footListener) {
        hasFootView = true;
        mRecyclerView = rView;
        rView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if ((newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == getItemCount())
                    //                        || (newState == RecyclerView.SCROLL_STATE_SETTLING && lastVisibleItem +
                    // 1 == getItemCount())
                        ) {
                    changeMoreStatus(LOADING_MORE);
                    footListener.onLodeMore(BaseRecyclerAdapter.this);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (hasHeadView) {
            if (hasFootView) {
                return total.size() + 2;
            }
            return total.size() + 1;
        }
        if (hasFootView && total.size() != 0) {
            return total.size() + 1;
        }
        return total.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (hasHeadView) {
            if (position == 0) {
                return HEAD_TYPE;
            }
        }
        if (hasFootView) {
            if (position + 1 == getItemCount()) {
                return FOOT_TYPE;
            }
        }
        return NORMAL_TYPE;
    }

    @Override
    public BaseViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEAD_TYPE:
                return getHeadHolder(parent);
            case NORMAL_TYPE:
                return getViewHolder(parent);
            case FOOT_TYPE:
                return getFootHolder(parent);
            default:
                return getViewHolder(parent);
        }
    }

    /**
     * @return Headview的ViewHolder
     */
    public BaseViewHolder<T> getHeadHolder(ViewGroup parent) {
        return null;
    }

    /**
     * @return 普通ItemView的ViewHolder
     */
    public abstract BaseViewHolder<T> getViewHolder(ViewGroup parent);

    /**
     * @return Footview的ViewHolder
     */
    private BaseViewHolder<T> getFootHolder(ViewGroup parent) {
        View footView = UIUtils.inflate(R.layout.foot_recycler_view, parent, false);
        return new FootViewHolder<T>(footView);
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder<T> holder, int position) {
        if (hasHeadView) {
            position--;
        }
        if (holder.getItemViewType() == HEAD_TYPE) {
            // 头布局
            holder.setObject(object);
        }
        if (holder.getItemViewType() == NORMAL_TYPE) {
            if (listener != null) {
                holder.itemView.setTag(position);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.OnItemClickListener(BaseRecyclerAdapter.this, (int)v.getTag());
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        listener.OnItemLongClickListener(BaseRecyclerAdapter.this, (int)v.getTag());
                        return true;
                    }
                });
            }
            holder.setData(total.get(position));
        }
        if (holder instanceof FootViewHolder) {
            footViewHolder = (FootViewHolder) holder;
        }
    }

    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        if (footViewHolder != null) {
            switch (status) {
                case PULLUP_LOAD_MORE:
                    footViewHolder.text_foot.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    footViewHolder.text_foot.setText("正在加载更多数据...");
                    break;
                case NO_MORE_DATA:
                    footViewHolder.text_foot.setText("已经到底啦...");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 设置监听回调
     *
     * @param listener
     */
    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * 增加数据并 刷新列表
     *
     * @param data
     */
    public void addMoreData(List<T> data) {
        total.addAll(data);
        notifyItemRangeInserted(total.size() - data.size(), data.size());
    }

    /**
     * 刷新数据并 刷新列表
     */
    public void refreshData(List<T> data) {
        total.clear();
        total.addAll(data);
        notifyDataSetChanged();
        changeMoreStatus(PULLUP_LOAD_MORE);
    }

    /**
     * 增加一条数据并 刷新列表
     */
    public void addItemData(T data) {
        total.add(data);
        notifyItemInserted(total.size() - 1);
    }

    public List<T> getData() {
        return total;
    }

    /**
     * RecyclerView 为空时,
     *
     * @param emptyView 显示的View
     */
    public void setEmptyView(View emptyView) {
        if (getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public static interface OnFootRefreshListener {
        void onLodeMore(BaseRecyclerAdapter baseAdapter);
    }

    /**
     * RecyclerView 中 ViewHolder 的基类
     */
    public static abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
        public BaseActivity mActivity = ActControl.getTop();

        public BaseViewHolder(View itemView) {
            super(itemView);
            initView(itemView);
        }

        /**
         * 初始化控件
         *
         * @param itemView
         */
        protected abstract void initView(View itemView);

        /**
         * 给控件适配数据
         *
         * @param data
         */
        protected abstract void setData(T data);

        /**
         * 给控件适配数据
         *
         * @param object
         */
        protected void setObject(Object object) {
        }
    }

    /**
     * RecyclerView 中的 FootView
     */
    private static class FootViewHolder<T> extends BaseViewHolder<T> {
        public TextView text_foot;

        public FootViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void initView(View itemView) {
            text_foot = (TextView) itemView.findViewById(R.id.text_foot);
        }

        @Override
        protected void setData(T data) {

        }
    }
}
