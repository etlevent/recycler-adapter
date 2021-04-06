package ext.android.adapter;

import androidx.recyclerview.widget.RecyclerView;

import ext.android.adapter.delegate.ItemViewDelegate;

/**
 * Created by Administrator on 2017/6/19.
 */

public interface OneToManyDelegate<T> {
    OneToManyConverter<T> bindDelegate(ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder>... delegates);
}
