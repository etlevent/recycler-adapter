package cherry.android.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ROOT on 2017/7/28.
 */

public class ExtendRecyclerView extends RecyclerView {

    private View mEmptyView;

    public ExtendRecyclerView(Context context) {
        super(context);
    }

    public ExtendRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null)
            oldAdapter.unregisterAdapterDataObserver(mObserver);
        super.setAdapter(adapter);
        if (adapter != null)
            adapter.registerAdapterDataObserver(mObserver);
        checkIfEmpty();
    }

    public void setEmptyView(View view) {
        this.mEmptyView = view;
    }

    private void checkIfEmpty() {
        if (this.mEmptyView != null && getAdapter() != null) {
            final boolean isEmpty = getAdapter().getItemCount() == 0;
            this.mEmptyView.setVisibility(isEmpty ? VISIBLE : GONE);
            setVisibility(isEmpty ? GONE : VISIBLE);
        }
    }

    private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmpty();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            checkIfEmpty();
        }
    };

}
