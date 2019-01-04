package ext.android.adapter.loadmore;

import android.support.annotation.LayoutRes;

import ext.android.adapter.ViewHolder;
import ext.android.adapter.wrapper.LoadMoreWrapper;

public interface LoadMoreView {
    void onStateChanged(@LoadMoreWrapper.State int state, ViewHolder viewHolder);

    @LayoutRes
    int getLayoutId();
}
