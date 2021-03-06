package ext.android.adapter.wrapper;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

import ext.android.adapter.ViewHolder;


/**
 * Created by Administrator on 2017/6/6.
 */

public class HeaderAndFooterWrapper extends BaseWrapper {

    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;

    private final SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private final SparseArrayCompat<View> mFooterViews = new SparseArrayCompat<>();

    public HeaderAndFooterWrapper(RecyclerView.Adapter<? super RecyclerView.ViewHolder> adapter) {
        super(adapter);
    }


    public int getHeaderCount() {
        return mHeaderViews.size();
    }

    public int getFooterCount() {
        return mFooterViews.size();
    }

    @Override
    int getWrapperTopCount() {
        return getHeaderCount();
    }

    @Override
    boolean isWrapperViewPosition(int position) {
        return isHeaderViewPos(position) || isFooterViewPos(position);
    }

    @Override
    int getWrapperItemCount() {
        return getHeaderCount() + getFooterCount();
    }

    @Override
    void onBindWrapperViewHolder(RecyclerView.ViewHolder holder, int position) {
        // do nothing
    }

    @Override
    int getWrapperItemType(int position) {
        if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position))
            return mFooterViews.keyAt(position - getHeaderCount() - getRealItemCount());
        throw new IllegalStateException("cannot match a wrapper ItemType: " + position);
    }

    @Override
    RecyclerView.ViewHolder onCreateWrapperViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null) {
            return new ViewHolder(mHeaderViews.get(viewType));
        } else if (mFooterViews.get(viewType) != null) {
            return new ViewHolder(mFooterViews.get(viewType));
        }
        throw new IllegalArgumentException("cannot create wrapper ViewHolder: " + viewType);
    }

    private boolean isHeaderViewPos(int position) {
        return position < getHeaderCount();
    }

    private boolean isFooterViewPos(int position) {
        return position >= getHeaderCount() + getRealItemCount();
    }

    public void addHeaderView(@NonNull View view) {
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    public void addFooterView(@NonNull View view) {
        mFooterViews.put(mFooterViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }
}
