package cherry.android.recycler.wrapper;

import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cherry.android.recycler.ViewHolder;

import static cherry.android.recycler.wrapper.LoadMoreWrapper.State.HIDE;
import static cherry.android.recycler.wrapper.LoadMoreWrapper.State.LOADING_FAIL;
import static cherry.android.recycler.wrapper.LoadMoreWrapper.State.LOADING_MORE;
import static cherry.android.recycler.wrapper.LoadMoreWrapper.State.NO_MORE;

public class LoadMoreWrapper extends BaseWrapper {
    private static final int ITEM_TYPE_LOAD_MORE = Integer.MAX_VALUE - 2;
    @LayoutRes
    private int mLayoutId;
    @State
    private int mState;
    private OnLoadMoreListener mLoadMoreListener;
    private OnStateChangedListener mOnStateChangedListener;

    public LoadMoreWrapper(@NonNull RecyclerView.Adapter adapter, @LayoutRes int layoutId) {
        super(adapter);
        this.mLayoutId = layoutId;
    }

    private boolean hasLoadMore() {
        return this.mLayoutId != 0;
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
        Log.d("Recycler", "onBindWrapperViewHolder " + position);
        Log.e("Recycler", "onBindWrapperViewHolder lastVisiblePosition=" + findLastVisibleItemPosition() + ", itemCount=" + getItemCount());
//        if (findLastVisibleItemPosition() < position - 1) {
//            Log.w("Recycler", "return with state changed.");
//            return;
//        }
        if (mOnStateChangedListener != null) {
            mOnStateChangedListener.onStateChanged(mState, (ViewHolder) holder);
        }
        if (mLoadMoreListener != null) {
            if (mState == LOADING_MORE) {
                mLoadMoreListener.onLoadMore();
                holder.itemView.setVisibility(View.VISIBLE);
            } else if (mState == LOADING_FAIL) {
                mLoadMoreListener.onLoadingFail(holder.itemView);
                holder.itemView.setVisibility(View.VISIBLE);
            } else if (mState == NO_MORE) {
                mLoadMoreListener.onNoMore(holder.itemView);
                holder.itemView.setVisibility(View.VISIBLE);
            } else if (mState == HIDE) {
                holder.itemView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    int getWrapperItemType(int position) {
        return ITEM_TYPE_LOAD_MORE;
    }

    @Override
    RecyclerView.ViewHolder onCreateWrapperViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Log.i("Recycler", "onAttachedToRecyclerView");
    }

    public void setState(@State int state) {
        mState = state;
        Log.e("Recycler", "lastVisiblePosition=" + findLastVisibleItemPosition() + ", itemCount=" + getItemCount()
                + ", wrapperItemCount=" + getWrapperItemCount());
        if (findLastVisibleItemPosition() >= getItemCount() - getWrapperItemCount()) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public int findLastVisibleItemPosition() {
        if (mAttachedRecyclerView == null) {
            return -1;
        }
        final RecyclerView.LayoutManager layoutManager = mAttachedRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
            int position = lastVisibleItemPositions[0];
            for (int itemPosition : lastVisibleItemPositions) {
                if (itemPosition > position) {
                    position = itemPosition;
                }
            }
        }
        return handleFindLastVisibleItemPosition(mAttachedRecyclerView);
    }

    public int handleFindLastVisibleItemPosition(RecyclerView recyclerView) {
        return -1;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    public void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
        this.mOnStateChangedListener = onStateChangedListener;
    }

    @IntDef({
            HIDE,
            LOADING_MORE,
            NO_MORE,
            LOADING_FAIL
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
        int HIDE = 0;
        int LOADING_MORE = 1;
        int NO_MORE = 2;
        int LOADING_FAIL = 3;
    }

    public interface OnStateChangedListener {
        void onStateChanged(@State int state, ViewHolder viewHolder);
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
