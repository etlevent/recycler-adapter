package ext.android.adapter.loadmore;

import androidx.annotation.LayoutRes;

import ext.android.adapter.ViewHolder;
import ext.android.adapter.wrapper.LoadMoreWrapper;

public interface LoadMoreView {
    void onStateChanged(@LoadMoreWrapper.State int state, ViewHolder viewHolder);

    @LayoutRes
    int getLayoutId();
}
