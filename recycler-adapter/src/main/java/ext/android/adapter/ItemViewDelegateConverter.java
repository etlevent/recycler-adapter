package ext.android.adapter;

import ext.android.adapter.delegate.ItemViewDelegate;

/**
 * Created by Administrator on 2017/6/19.
 */

public interface ItemViewDelegateConverter<T> {
    Class<? extends ItemViewDelegate> convert(T t, int position);
}
