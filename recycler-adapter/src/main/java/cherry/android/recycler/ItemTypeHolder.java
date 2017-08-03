package cherry.android.recycler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Administrator on 2017/6/19.
 */

public class ItemTypeHolder<T> {
    private Class<? extends T> itemTypeClass;
    private SparseArrayCompat<ItemViewDelegate<T, ? extends RecyclerView.ViewHolder>> delegates = new SparseArrayCompat<>(1);
    private ViewChooser<T> chooser;

    protected ItemTypeHolder(@NonNull Class<? extends T> itemTypeClass,
                             @Nullable ViewChooser<T> chooser,
                             @NonNull ItemViewDelegate<T, ? extends RecyclerView.ViewHolder>... delegates) {
        this.itemTypeClass = itemTypeClass;
        this.chooser = chooser;
        for (int i = 0; i < delegates.length; i++) {
            this.delegates.put(this.delegates.size(), delegates[i]);
        }
    }

    protected ItemTypeHolder(Class<? extends T> itemTypeClass,
                             ItemViewDelegate<T, ? extends RecyclerView.ViewHolder> delegate) {
        this.itemTypeClass = itemTypeClass;
        this.delegates.put(this.delegates.size(), delegate);
    }

    protected ItemViewDelegate getItemViewDelegate(@NonNull Object item, int position) {
        if (this.chooser == null) {
            return delegates.get(0);
        }
        Class itemViewClass = this.chooser.choose((T) item, position);
        for (int i = 0; i < delegates.size(); i++) {
            ItemViewDelegate delegate = delegates.get(i);
            if (delegate.getClass().equals(itemViewClass)) {
                return delegate;
            }
        }
        throw new IllegalArgumentException("cannot find using class: " + itemViewClass.getSimpleName());
    }

    protected Class<? extends T> getItemClass() {
        return this.itemTypeClass;
    }
}
