package boxing.websocketclient.interfaces;


import boxing.websocketclient.base.BaseRecyclerAdapter;

/**
 * RecyclerView item 的点击回调事件
 * Created by liuyuli on 2017/6/1 0001.
 */

public interface ItemClickListener<T> {
    void OnItemClickListener(BaseRecyclerAdapter<T> adapter, int position);
    void OnItemLongClickListener(BaseRecyclerAdapter<T> adapter, int position);
}
