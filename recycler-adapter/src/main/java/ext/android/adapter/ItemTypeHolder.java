package ext.android.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

import ext.android.adapter.delegate.ItemViewDelegate;

/**
 * Created by Administrator on 2017/6/19.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class ItemTypeHolder<T> {
    private final Class<? extends T> itemTypeClass;
    private final SparseArrayCompat<ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder>> delegates = new SparseArrayCompat<>(1);
    private ItemViewDelegateConverter<T> converter;

    @SafeVarargs
    public ItemTypeHolder(@NonNull Class<? extends T> itemTypeClass,
                          @Nullable ItemViewDelegateConverter<T> converter,
                          @NonNull ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder>... delegates) {
        this.itemTypeClass = itemTypeClass;
        this.converter = converter;
        for (ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder> delegate : delegates) {
            this.delegates.put(this.delegates.size(), delegate);
        }
    }

    public ItemTypeHolder(Class<? extends T> itemTypeClass,
                          ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder> delegate) {
        this.itemTypeClass = itemTypeClass;
        this.delegates.put(this.delegates.size(), delegate);
    }

    @SuppressWarnings("unchecked")
    public ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder> getItemViewDelegate(@NonNull Object item, int position) {
        if (this.converter == null) {
            return delegates.get(0);
        }
        Class<?> itemViewClass = this.converter.convert((T) item, position);
        for (int i = 0; i < delegates.size(); i++) {
            ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder> delegate = delegates.get(i);
            if (delegate == null) continue;
            if (itemViewClass.isAssignableFrom(delegate.getClass())) {
                return delegate;
            }
        }
        throw new IllegalArgumentException("cannot find using class: " + itemViewClass.getSimpleName());
    }

    public Class<? extends T> getItemClass() {
        return this.itemTypeClass;
    }
}
