package ext.android.adapter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ext.android.adapter.diff.DiffCapable;

/**
 * Created by Administrator on 2017/6/6.
 */

public class RecyclerAdapter extends AbstractRecyclerAdapter {

    private static final String TAG = "RecyclerAdapter";

    private static final int MSG_DIFF_RESULT = 0;
    private List<?> mItems;
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

    public RecyclerAdapter() {
    }

    public RecyclerAdapter(@Nullable List<?> items) {
        mItems = items;
    }

    @Nullable
    @Override
    protected Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mAttached = true;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mAttached = false;
        mHandler.removeCallbacksAndMessages(null);
        closeExecutor();
    }

    @Nullable
    public List<?> getItems() {
        return this.mItems;
    }

    public void setItems(@Nullable List<?> items) {
        this.mItems = items;
        notifyDataSetChanged();
    }

    public <P> void setItems(@Nullable List<?> items, DiffCapable<P> diffCapable) {
        final boolean diff = diff(this.mItems, items, diffCapable);
        this.mItems = items;
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

    private static class DiffCallback<P> extends DiffUtil.Callback {

        private final List<?> oldItems;
        private final List<?> newItems;
        private final DiffCapable<P> diffCapable;

        DiffCallback(List<?> oldItems, List<?> newItems, DiffCapable<P> diffCapable) {
            this.oldItems = oldItems;
            this.newItems = newItems;
            this.diffCapable = diffCapable;
        }

        @Override
        public int getOldListSize() {
            return this.oldItems != null ? this.oldItems.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return this.newItems != null ? this.newItems.size() : 0;
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