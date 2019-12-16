package ext.android.adapter.delegate;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;

import java.util.Collection;

import ext.android.adapter.ItemTypeHolder;
import ext.android.adapter.ItemViewDelegateConverter;

/**
 * Created by Administrator on 2017/6/19.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class ItemViewDelegateManager {
    private final SparseArrayCompat<ItemViewDelegate<?, ? extends RecyclerView.ViewHolder>> delegates = new SparseArrayCompat<>();
    private final ArrayMap<Class<?>, ItemTypeHolder<?>> typeMap = new ArrayMap<>();

    private ItemViewDelegateManager() {

    }

    public static ItemViewDelegateManager get() {
        return new ItemViewDelegateManager();
    }

    /**
     * 添加Class - item映射, 单一映射
     *
     * @param clazz
     * @param delegate
     * @param <T>
     * @param <VH>
     */
    @SuppressWarnings("unchecked")
    public <T, VH extends RecyclerView.ViewHolder> void addDelegate(@NonNull Class<? extends T> clazz,
                                                                    @NonNull ItemViewDelegate<? extends T, VH> delegate) {
        if (delegate == null || clazz == null) {
            throw new NullPointerException("class or delegate should not be NULL!");
        }
        delegates.put(delegates.size(), delegate);
        typeMap.put(clazz, new ItemTypeHolder(clazz, delegate));
    }

    /**
     * 添加Class - items映射, 一对多映射关系
     *
     * @param clazz item class
     * @param converter
     * @param delegates
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public <T> void addDelegate(@NonNull Class<? extends T> clazz,
                                @NonNull ItemViewDelegateConverter<T> converter,
                                @NonNull ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder>... delegates) {
        if (delegates == null || clazz == null) {
            throw new NullPointerException("class or delegates should not be NULL!");
        }
        for (ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder> delegate : delegates) {
            this.delegates.put(this.delegates.size(), delegate);
        }
        typeMap.put(clazz, new ItemTypeHolder(clazz, converter, delegates));
    }

    public int getDelegateCount() {
        return delegates.size();
    }

    /**
     * 获取对应的item Type, 为保证ItemType的唯一性, 采用delegates列表中的下标
     *
     * @param item
     * @param position
     * @return
     */
    @SuppressWarnings("unchecked")
    public int getItemViewType(final Object item, final int position) {
        Class<?> itemClass = item.getClass();
        ItemTypeHolder<?> itemTypeHolder = getItemTypeHolder(itemClass);
        if (itemTypeHolder != null) {
            ItemViewDelegate delegate = itemTypeHolder.getItemViewDelegate(item, position);
            return delegates.indexOfValue(delegate);
        }
        String msg = "Cannot find itemType according item: [" +
                item +
                "], @ position=[" +
                position +
                ']' +
                ", item class=[" +
                item.getClass() +
                ']';
        throw new IllegalArgumentException(msg);
    }

    /**
     * viewType即delegate在列表中的下标
     *
     * @param viewType
     * @return
     */
    public ItemViewDelegate getItemViewDelegate(int viewType) {
        return delegates.get(viewType);
    }

    @Nullable
    private ItemTypeHolder getItemTypeHolder(Class<?> clazz) {
        Collection<ItemTypeHolder<?>> holders = typeMap.values();
        for (ItemTypeHolder<?> itemTypeHolder : holders) {
            if (clazz.equals(itemTypeHolder.getItemClass())) {
                return itemTypeHolder;
            }
        }
        for (ItemTypeHolder<?> itemTypeHolder : holders) {
            if (itemTypeHolder.getItemClass().isAssignableFrom(clazz)) {
                return itemTypeHolder;
            }
        }
        return null;
    }
}
