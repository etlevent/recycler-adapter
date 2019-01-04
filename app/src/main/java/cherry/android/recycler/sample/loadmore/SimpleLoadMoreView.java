package cherry.android.recycler.sample.loadmore;

import android.view.View;

import ext.android.adapter.ViewHolder;
import ext.android.adapter.loadmore.LoadMoreView;
import cherry.android.recycler.sample.R;
import ext.android.adapter.wrapper.LoadMoreWrapper;

public class SimpleLoadMoreView implements LoadMoreView {
    @Override
    public void onStateChanged(int state, ViewHolder viewHolder) {
        View loadingView = viewHolder.findView(R.id.loading);
        View noMoreView = viewHolder.findView(R.id.no_more_view);
        switch (state) {
            case LoadMoreWrapper.STATE_LOADING_MORE:
                viewHolder.itemView.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.VISIBLE);
                noMoreView.setVisibility(View.GONE);
                break;
            case LoadMoreWrapper.STATE_LOADING_FAIL:
                viewHolder.itemView.setVisibility(View.VISIBLE);
                break;
            case LoadMoreWrapper.STATE_NO_MORE:
                viewHolder.itemView.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.GONE);
                noMoreView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_loading_more;
    }
}
