package cherry.android.recycler.wrapper;

import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cherry.android.recycler.R;
import cherry.android.recycler.ViewHolder;


/**
 * Created by Administrator on 2017/6/16.
 */

public class LoadMoreWrapper extends BaseWrapper {
    private static final int ITEM_TYPE_LOAD_MORE = Integer.MAX_VALUE - 2;
    public static final int STATE_HIDE = 0;
    public static final int STATE_LOADING_MORE = 1;
    public static final int STATE_NO_MORE = 2;
    public static final int STATE_LOADING_FAIL = 3;

    @IntDef({STATE_HIDE, STATE_LOADING_MORE, STATE_NO_MORE, STATE_LOADING_FAIL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    private View mLoadMoreView;
    @LayoutRes
    private int mLoadMoreLayoutId;

    @State
    private int mState = STATE_HIDE;
    private OnLoadMoreListener mLoadMoreListener;

    public LoadMoreWrapper(@NonNull RecyclerView.Adapter adapter, @NonNull View loadMoreView) {
        super(adapter);
        mLoadMoreView = loadMoreView;
    }

    public LoadMoreWrapper(@NonNull RecyclerView.Adapter adapter, @LayoutRes int loadMoreLayoutId) {
        super(adapter);
        mLoadMoreLayoutId = loadMoreLayoutId;
    }

    public LoadMoreWrapper(@NonNull RecyclerView.Adapter adapter) {
        super(adapter);
    }

    @Override
    int getWrapperTopCount() {
        return 0;
    }

    @Override
    boolean isWrapperViewPosition(int position) {
        return hasLoadMore() && position >= getRealItemCount() && position > 0;
    }

    @Override
    int getWrapperItemCount() {
        return (hasLoadMore() ? 1 : 0);
    }

    @Override
    void onBindWrapperViewHolder(RecyclerView.ViewHolder holder, int position) {
        final boolean hide = mState == STATE_HIDE;
        if (hide) {
            holder.itemView.setVisibility(View.GONE);
            return;
        }
        final boolean loadingMore = mState == STATE_LOADING_MORE;
        final boolean loadingFail = mState == STATE_LOADING_FAIL;
        final boolean noMore = mState == STATE_NO_MORE;
        View noMoreView = null;
        View loadingFailView = null;
        if (holder.itemView instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) holder.itemView;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child.getId() == R.id.no_more_view) {
                    child.setVisibility(noMore ? View.VISIBLE : View.GONE);
                    noMoreView = child;
                } else if (child.getId() == R.id.loading_fail_view) {
                    child.setVisibility(loadingFail ? View.VISIBLE : View.GONE);
                    loadingFailView = child;
                } else {
                    child.setVisibility((noMore || loadingFail) ? View.GONE : View.VISIBLE);
                }
            }
        }
        if (mLoadMoreListener != null) {
            if (loadingMore && isDataCountChanged()) {
                mLoadMoreListener.onLoadMore();
                notifyItemCountChanged();
            } else if (noMore && noMoreView == null) {
                mLoadMoreListener.onNoMore(holder.itemView);
            } else if (loadingFail && loadingFailView == null) {
                mLoadMoreListener.onLoadingFail(holder.itemView);
            }
        }
    }

    @Override
    int getWrapperItemType(int position) {
        return ITEM_TYPE_LOAD_MORE;
    }

    @Override
    RecyclerView.ViewHolder onCreateWrapperViewHolder(ViewGroup parent, int viewType) {
        if (mLoadMoreLayoutId != 0) {
            mLoadMoreView = LayoutInflater.from(parent.getContext()).inflate(mLoadMoreLayoutId, parent, false);
        }
        return new ViewHolder(mLoadMoreView);
    }

    private boolean hasLoadMore() {
        return (mLoadMoreView != null || mLoadMoreLayoutId != 0)
                && mState != STATE_HIDE;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    @State
    public int getState() {
        return mState;
    }

    public void setState(@State int state) {
        mState = state;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();

        void onLoadingFail(View loadMoreView);

        void onNoMore(View loadMoreView);
    }

    public abstract static class SimpleLoadMoreListener implements OnLoadMoreListener {
        @Override
        public void onNoMore(View loadMoreView) {
        }

        @Override
        public void onLoadingFail(View loadMoreView) {

        }
    }
}