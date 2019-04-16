package ext.android.adapter.wrapper;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/16.
 */

public abstract class BaseWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final RecyclerView.Adapter mInnerAdapter;
    private final List<Integer> mWrapperViewTypeList;
    protected RecyclerView mAttachedRecyclerView;
    private final RecyclerView.AdapterDataObserver mDataObserver;

    public BaseWrapper(@NonNull RecyclerView.Adapter adapter) {
        mInnerAdapter = adapter;
        mWrapperViewTypeList = new ArrayList<>();
        mDataObserver = new WrapperAdapterDataObserver();
        adapter.registerAdapterDataObserver(mDataObserver);
    }

    @Override
    public final int getItemCount() {
        return getRealItemCount() + getWrapperItemCount();
    }

    @Override
    public final int getItemViewType(int position) {
        if (isWrapperViewPosition(position)) {
            int viewType = getWrapperItemType(position);
            mWrapperViewTypeList.add(viewType);
            return viewType;
        }
        return mInnerAdapter.getItemViewType(position - getWrapperTopCount());
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isWrapperViewType(viewType)) {
            return onCreateWrapperViewHolder(parent, viewType);
        }
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isWrapperViewPosition(position)) {
            onBindWrapperViewHolder(holder, position);
        } else {
            mInnerAdapter.onBindViewHolder(holder, position - getWrapperTopCount());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (isWrapperViewPosition(position)) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            mInnerAdapter.onBindViewHolder(holder, position - getWrapperTopCount(), payloads);
        }
    }

    @CallSuper
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mAttachedRecyclerView = recyclerView;
        mInnerAdapter.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isWrapperViewPosition(position)) {
                        return gridLayoutManager.getSpanCount();
                    }
                    if (spanSizeLookup != null) {
                        return spanSizeLookup.getSpanSize(position - getWrapperTopCount());
                    }
                    return 1;
                }
            });
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mAttachedRecyclerView = null;
    }

    @CallSuper
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        mInnerAdapter.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (isWrapperViewPosition(position)) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                lp.setFullSpan(true);
            }
        }
    }

    int getRealItemCount() {
        return mInnerAdapter.getItemCount();
    }

    private boolean isWrapperViewType(int viewType) {
        return mWrapperViewTypeList.size() != 0 && mWrapperViewTypeList.contains(viewType);
    }

    abstract int getWrapperTopCount();

    abstract boolean isWrapperViewPosition(int position);

    abstract int getWrapperItemCount();

    abstract void onBindWrapperViewHolder(RecyclerView.ViewHolder holder, int position);

    abstract int getWrapperItemType(int position);

    abstract RecyclerView.ViewHolder onCreateWrapperViewHolder(ViewGroup parent, int viewType);

    private class WrapperAdapterDataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart + getWrapperTopCount(), itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            notifyItemRangeChanged(positionStart + getWrapperTopCount(), itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            notifyItemRangeInserted(positionStart + getWrapperTopCount(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            notifyItemRangeRemoved(positionStart + getWrapperTopCount(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            notifyItemMoved(fromPosition + getWrapperTopCount(), toPosition + getWrapperTopCount());
        }
    }
}
