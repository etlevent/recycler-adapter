package ext.android.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ext.android.adapter.delegate.ItemViewDelegate;
import ext.android.adapter.delegate.ItemViewDelegateManager;
import ext.android.adapter.diff.PayloadsCapable;

public abstract class AbstractRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "RecyclerAdapter";

    private final ItemViewDelegateManager mDelegateManager;

    private RecyclerAdapter.OnItemClickListener mItemClickListener;
    private RecyclerAdapter.OnItemLongClickListener mItemLongClickListener;
    private int mPositionOffset = 0;

    public AbstractRecyclerAdapter() {
        mDelegateManager = ItemViewDelegateManager.get();
    }

    @Override
    public final int getItemViewType(int position) {
        if (!isDelegateNotEmpty()) return super.getItemViewType(position);
        if (getItemCount() == 0) return super.getItemViewType(position);
        Object item = getItem(position);
        if (item == null) return super.getItemViewType(position);
        return mDelegateManager.getItemViewType(item, position);
    }

    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemViewDelegate<?, ? extends RecyclerView.ViewHolder> delegate = mDelegateManager.getItemViewDelegate(viewType);
        RecyclerView.ViewHolder holder = delegate.createViewHolder(inflater, parent);
        setListener(holder.itemView, holder);
        return holder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemCount() == 0) return;
        int viewType = holder.getItemViewType();
        ItemViewDelegate<Object, RecyclerView.ViewHolder> delegate = (ItemViewDelegate<Object, RecyclerView.ViewHolder>) mDelegateManager.getItemViewDelegate(viewType);
        // fix position refused with item size changed
        mPositionOffset = holder.getAdapterPosition() - position;
        // holder.itemView.setTag(R.id.item_position, position);
        delegate.convert(holder, getItem(position), position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            int viewType = holder.getItemViewType();
            ItemViewDelegate<?, ? extends RecyclerView.ViewHolder> delegate = mDelegateManager.getItemViewDelegate(viewType);
            if (PayloadsCapable.class.isAssignableFrom(delegate.getClass())) {
                PayloadsCapable payloadsCapable = (PayloadsCapable) delegate;
                holder.itemView.setTag(R.id.item_position, position);
                payloadsCapable.payloads(holder, position, payloads);
            } else {
                Log.e(TAG, "data has payloads, but not have impl payloadsCapable. " + delegate.getClass().getSimpleName());
                super.onBindViewHolder(holder, position, payloads);
            }
        }
    }

    @Nullable
    protected abstract Object getItem(int position);

    private boolean isDelegateNotEmpty() {
        return mDelegateManager.getDelegateCount() > 0;
    }

    private void setListener(final View itemView, final RecyclerView.ViewHolder holder) {
        if (!itemView.hasOnClickListeners()) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        // fix position refused with item size changed
                        int adapterPosition = holder.getAdapterPosition();
                        //int position2 = holder.getLayoutPosition();
                        /*real position except header*/
                        //int position = (int) itemView.getTag(R.id.item_position);
                        int position = adapterPosition - mPositionOffset;
                        mItemClickListener.onItemClick(itemView, holder, position);
                    }
                }
            });
        }
        if (!itemView.isLongClickable()) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mItemLongClickListener != null) {
                        // fix position refused with item size changed
                        int adapterPosition = holder.getAdapterPosition();
                        /*real position except header*/
                        //int position = (int) itemView.getTag(R.id.item_position);
                        int position = adapterPosition - mPositionOffset;
                        return mItemLongClickListener.onItemLongClick(itemView, holder, position);
                    }
                    return false;
                }
            });
        }
    }

    public <T, VH extends RecyclerView.ViewHolder> void addDelegate(@NonNull Class<? extends T> clazz,
                                                                    @NonNull ItemViewDelegate<? extends T, VH> delegate) {
        mDelegateManager.addDelegate(clazz, delegate);
    }

    public <T> OneToManyDelegate<T> addDelegate(@NonNull Class<? extends T> clazz) {
        return new OneToManyWrapper<>(clazz, mDelegateManager);
    }

    public void setOnItemClickListener(RecyclerAdapter.OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(RecyclerAdapter.OnItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, RecyclerView.ViewHolder holder, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View itemView, RecyclerView.ViewHolder holder, int position);
    }
}