package cherry.android.recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2017/6/6.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<?> mItems;
    protected final ItemViewDelegateManager mDelegateManager;
    private LayoutInflater mInflater;

    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    public RecyclerAdapter(List<?> items) {
        mItems = items;
        mDelegateManager = ItemViewDelegateManager.get();
    }

    public RecyclerAdapter() {
        mDelegateManager = ItemViewDelegateManager.get();
    }

    @Override
    public final int getItemViewType(int position) {
        if (!useDelegate()) return super.getItemViewType(position);
        if (mItems == null) return super.getItemViewType(position);
        Object item = mItems.get(position);
        return mDelegateManager.getItemViewType(item, position);
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null)
            mInflater = LayoutInflater.from(parent.getContext());
        ItemViewDelegate delegate = mDelegateManager.getItemViewDelegate(viewType);
        RecyclerView.ViewHolder holder = delegate.createViewHolder(mInflater, parent);
        setListener(holder.itemView, holder);
        return holder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mItems == null) return;
        int viewType = holder.getItemViewType();
        ItemViewDelegate delegate = mDelegateManager.getItemViewDelegate(viewType);
        holder.itemView.setTag(R.id.item_position, position);
        delegate.convert(holder, mItems.get(position), position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    protected boolean useDelegate() {
        return mDelegateManager.getDelegateCount() > 0;
    }

    private void setListener(final View itemView, final RecyclerView.ViewHolder holder) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
//                    int position = holder.getAdapterPosition();
                    int position = (int) itemView.getTag(R.id.item_position);
                    mItemClickListener.onItemClick(itemView, holder, position);
                }
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemLongClickListener != null) {
//                    int position = holder.getAdapterPosition();
                    int position = (int) itemView.getTag(R.id.item_position);
                    return mItemLongClickListener.onItemLongClick(itemView, holder, position);
                }
                return false;
            }
        });
    }

    public <T, VH extends RecyclerView.ViewHolder> void addDelegate(@NonNull Class<? extends T> clazz,
                                                                    @NonNull ItemViewDelegate<T, VH> delegate) {
        mDelegateManager.addDelegate(clazz, delegate);
    }

    public <T> OneToManyDelegate<T> addDelegate(@NonNull Class<? extends T> clazz) {
        return new OneToManyWrapper<>(clazz, mDelegateManager);
    }

    public List<?> getItems() {
        return this.mItems;
    }

    public void setItems(List<?> items) {
        this.mItems = items;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, RecyclerView.ViewHolder holder, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View itemView, RecyclerView.ViewHolder holder, int position);
    }
}