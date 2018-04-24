package cherry.android.recycler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cherry.android.recycler.diff.DiffCapable;
import cherry.android.recycler.diff.PayloadsCapable;

/**
 * Created by Administrator on 2017/6/6.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "RecyclerAdapter";

    private static final int MSG_DIFF_RESULT = 0;
    protected final ItemViewDelegateManager mDelegateManager;
    @NonNull
    protected List<?> mItems;
    private LayoutInflater mInflater;

    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    private boolean mAttached;
    private ThreadPoolExecutor mExecutor;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_DIFF_RESULT && mAttached) {
                final DiffUtil.DiffResult result = (DiffUtil.DiffResult) msg.obj;
                if (result != null) {
                    result.dispatchUpdatesTo(RecyclerAdapter.this);
                }
            }
        }
    };

    public RecyclerAdapter(@Nullable List<?> items) {
        mItems = transferUnmodifiable(items);
        mDelegateManager = ItemViewDelegateManager.get();
    }

    public RecyclerAdapter() {
        mDelegateManager = ItemViewDelegateManager.get();
    }

    @NonNull
    private static List<?> transferUnmodifiable(@Nullable List<?> items) {
        if (items == null) {
            items = Collections.EMPTY_LIST;
        }
        return Collections.unmodifiableList(items);
    }

    @Override
    public final int getItemViewType(int position) {
        if (!useDelegate()) return super.getItemViewType(position);
        if (mItems.isEmpty()) return super.getItemViewType(position);
        Object item = mItems.get(position);
        return mDelegateManager.getItemViewType(item, position);
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        ItemViewDelegate delegate = mDelegateManager.getItemViewDelegate(viewType);
        RecyclerView.ViewHolder holder = delegate.createViewHolder(mInflater, parent);
        setListener(holder.itemView, holder);
        return holder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mItems.isEmpty()) return;
        int viewType = holder.getItemViewType();
        ItemViewDelegate delegate = mDelegateManager.getItemViewDelegate(viewType);
        holder.itemView.setTag(R.id.item_position, position);
        delegate.convert(holder, mItems.get(position), position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            int viewType = holder.getItemViewType();
            ItemViewDelegate delegate = mDelegateManager.getItemViewDelegate(viewType);
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
                    /*int position = holder.getAdapterPosition();*/
                    /*real position except header*/
                    int position = (int) itemView.getTag(R.id.item_position);
                    mItemClickListener.onItemClick(itemView, holder, position);
                }
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemLongClickListener != null) {
                    /*int position = holder.getAdapterPosition();*/
                    /*real position except header*/
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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Log.v(TAG, "onAttachedToRecyclerView");
        mAttached = true;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        Log.v(TAG, "onDetachedFromRecyclerView");
        mAttached = false;
        mHandler.removeCallbacksAndMessages(null);
        closeExecutor();
    }

    @Nullable
    public List<?> getItems() {
        return this.mItems;
    }

    public void setItems(@Nullable List<?> items) {
        this.mItems = transferUnmodifiable(items);
        notifyDataSetChanged();
    }

    public <P> void setItems(@Nullable List<?> items, DiffCapable<P> diffCapable) {
        final boolean diff = diff(this.mItems, items, diffCapable);
        this.mItems = transferUnmodifiable(items);
        if (!diff) {
            notifyDataSetChanged();
        }
    }

    private <P> boolean diff(final List<?> oldItems, final List<?> newItems, final DiffCapable<P> diffCapable) {
        if (mExecutor == null) {
            mExecutor = new ThreadPoolExecutor(5,
                    10,
                    500,
                    TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(10));
            mExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        }
        if (mExecutor.isShutdown()) {
            return false;
        }
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback<>(oldItems, newItems, diffCapable));
                mHandler.obtainMessage(MSG_DIFF_RESULT, diffResult).sendToTarget();
            }
        });
        return true;
    }

    private void closeExecutor() {
        if (mExecutor == null || mExecutor.isShutdown()) {
            return;
        }
        mExecutor.shutdown();
        try {
            if (!mExecutor.awaitTermination(20, TimeUnit.SECONDS)) {
                mExecutor.shutdown();
                if (mExecutor.awaitTermination(20, TimeUnit.SECONDS)) {
                    Log.e(TAG, "executor not terminate");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            mExecutor.shutdownNow();
        }
        mExecutor = null;
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

    private static class DiffCallback<P> extends DiffUtil.Callback {

        private final List<?> oldItems;
        private final List<?> newItems;
        private final DiffCapable<P> diffCapable;

        public DiffCallback(List<?> oldItems, List<?> newItems, DiffCapable<P> diffCapable) {
            this.oldItems = transferUnmodifiable(oldItems);
            this.newItems = transferUnmodifiable(newItems);
            this.diffCapable = diffCapable;
        }

        @Override
        public int getOldListSize() {
            return this.oldItems.size();
        }

        @Override
        public int getNewListSize() {
            return this.newItems.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if (this.diffCapable != null) {
                final Object oldItem = this.oldItems.get(oldItemPosition);
                final Object newItem = this.newItems.get(newItemPosition);
                return this.diffCapable.isSame(DiffCapable.DIFF_ITEM, oldItem, newItem);
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            if (this.diffCapable != null) {
                final Object oldItem = this.oldItems.get(oldItemPosition);
                final Object newItem = this.newItems.get(newItemPosition);
                return this.diffCapable.isSame(DiffCapable.DIFF_CONTENT, oldItem, newItem);
            }
            return true;
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            Log.i(TAG, "[getChangePayload] oldItemPosition=" + oldItemPosition + ", newItemPosition=" + newItemPosition);
            if (this.diffCapable != null) {
                final Object oldItem = this.oldItems.get(oldItemPosition);
                final Object newItem = this.newItems.get(newItemPosition);
                return this.diffCapable.payloads(oldItem, newItem);
            }
            return super.getChangePayload(oldItemPosition, newItemPosition);
        }
    }
}