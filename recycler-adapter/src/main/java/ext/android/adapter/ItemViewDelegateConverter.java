package ext.android.adapter;

import androidx.recyclerview.widget.RecyclerView;

import ext.android.adapter.delegate.ItemViewDelegate;

/**
 * Created by Administrator on 2017/6/19.
 */

public interface ItemViewDelegateConverter<T> {
    Class<? extends ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder>> convert(T t, int position);
}
