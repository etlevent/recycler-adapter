package ext.android.adapter.wrapper;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ext.android.adapter.ViewHolder;
import ext.android.adapter.loadmore.LoadMoreView;

public class LoadMoreWrapper extends BaseWrapper {
    private static final int ITEM_TYPE_LOAD_MORE = Integer.MAX_VALUE - 2;
    public static final int STATE_LOADING_MORE = 0;
    public static final int STATE_NO_MORE = 1;
    public static final int STATE_LOADING_FAIL = 2;
    private final LoadMoreView mLoadMoreView;
    private boolean mIsLoadMoreEnabled;
    @State
    private int mState;
    private OnLoadMoreListener mLoadMoreListener;

    public LoadMoreWrapper(@NonNull RecyclerView.Adapter<? super RecyclerView.ViewHolder> adapter, @Nullable LoadMoreView loadMoreView) {
        super(adapter);
        this.mLoadMoreView = loadMoreView;
        this.registerAdapterDataObserver(new LoadMoreAdapterDataObserver());
    }

    private boolean hasLoadMore() {
        return this.mIsLoadMoreEnabled && this.mLoadMoreView != null && this.mLoadMoreView.getLayoutId() != 0;
    }

    @Override
    int getWrapperTopCount() {
        return 0;
    }

    @Override
    boolean isWrapperViewPosition(int position) {
        return hasLoadMore() && position >= getRealItemCount();
    }

    @Override
    int getWrapperItemCount() {
        return hasLoadMore() ? 1 : 0;
    }

    @Override
    void onBindWrapperViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (this.mLoadMoreView != null) {
            this.mLoadMoreView.onStateChanged(mState, (ViewHolder) holder);
        }
        if (mLoadMoreListener != null) {
            if (mState == STATE_LOADING_MORE) {
                if (mAttachedRecyclerView != null) {
                    mAttachedRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mLoadMoreListener.onLoadMore();
                        }
                    });
                } else {
                    mLoadMoreListener.onLoadMore();
                }
            }
        }
    }

    @Override
    int getWrapperItemType(int position) {
        return ITEM_TYPE_LOAD_MORE;
    }

    @Override
    RecyclerView.ViewHolder onCreateWrapperViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(mLoadMoreView.getLayoutId(), parent, false));
    }

    public void setState(@State int state) {
        mState = state;
        if (!hasLoadMore()) {
            return;
        }
        notifyItemChanged(getItemCount() - 1);
    }

    private void decideIfLoadMoreEnable() {
        if (mAttachedRecyclerView == null) {
            return;
        }
        mAttachedRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                final RecyclerView.LayoutManager layoutManager = mAttachedRecyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    final int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
                    final int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();
                    boolean isChildFullPage = !(firstVisiblePosition == 0 && lastVisiblePosition == getRealItemCount() - 1);
                    setLoadMoreEnabled(isChildFullPage);
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
                    int position = lastVisibleItemPositions[0];
                    for (int itemPosition : lastVisibleItemPositions) {
                        if (itemPosition > position) {
                            position = itemPosition;
                        }
                    }
                    setLoadMoreEnabled(position != getRealItemCount() - 1);
                }
            }
        });

    }

    private void setLoadMoreEnabled(boolean enabled) {
        if (enabled != mIsLoadMoreEnabled) {
            notifyItemChanged(getItemCount() - 1);
        }
        mIsLoadMoreEnabled = enabled;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    @IntDef({
            STATE_LOADING_MORE,
            STATE_NO_MORE,
            STATE_LOADING_FAIL
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private class LoadMoreAdapterDataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            decideIfLoadMoreEnable();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            decideIfLoadMoreEnable();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            decideIfLoadMoreEnable();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            decideIfLoadMoreEnable();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            decideIfLoadMoreEnable();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            decideIfLoadMoreEnable();
        }
    }
}
