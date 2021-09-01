package ext.android.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AdapterListUpdateCallback;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Collections;
import java.util.List;


public class RecyclerAdapter extends AbstractRecyclerAdapter {
    private static final String TAG = "RecyclerAdapter";
    private final AsyncListDiffer mDiffer;
    private List<?> mList;


    public RecyclerAdapter() {
        mDiffer = null;
    }

    public RecyclerAdapter(@Nullable List<?> list) {
        this();
        mList = list;
    }

    public RecyclerAdapter(@NonNull DiffUtil.ItemCallback<?> diffCallback) {
        mDiffer = new AsyncListDiffer<>(new AdapterListUpdateCallback(this),
                new AsyncDifferConfig.Builder<>(diffCallback).build());
    }

    public RecyclerAdapter(@NonNull AsyncDifferConfig<?> config) {
        mDiffer = new AsyncListDiffer<>(new AdapterListUpdateCallback(this), config);
    }

    @Nullable
    @Override
    protected Object getItem(int position) {
        return getList().get(position);
    }

    @Override
    public int getItemCount() {
        return getList().size();
    }

    @NonNull
    public List<?> getList() {
        if (mDiffer != null) {
            return mDiffer.getCurrentList();
        }
        if (this.mList == null) return Collections.emptyList();
        return Collections.unmodifiableList(this.mList);
    }

    public void setList(@Nullable List<?> list) {
        this.mList = list;
    }

    public void submitList(@Nullable List<?> list) {
        if (mDiffer == null) {
            throw new NullPointerException("specified AsyncListDiffer First");
        }
        mDiffer.submitList(list);
    }
}