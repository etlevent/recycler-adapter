package ext.android.adapter.delegate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.collection.ArrayMap;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

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
     * @param clazz    ItemType 对应的Class
     * @param delegate ItemType 对应布局代理
     * @param <T>      实体类型
     */
    public <T> void addDelegate(@NonNull Class<? extends T> clazz,
                                @NonNull ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder> delegate) {
        if (delegate == null || clazz == null) {
            throw new NullPointerException("class or delegate should not be NULL!");
        }
        delegates.put(delegates.size(), delegate);
        typeMap.put(clazz, new ItemTypeHolder<>(clazz, delegate));
    }

    /**
     * 添加Class - items映射, 一对多映射关系
     *
     * @param clazz     ItemType 对应的Class
     * @param converter 类型布局对应关系类
     * @param delegates ItemType 对应布局代理
     * @param <T>       实体类型
     */
    @SafeVarargs
    public final <T> void addDelegate(@NonNull Class<? extends T> clazz,
                                      @NonNull ItemViewDelegateConverter<T> converter,
                                      @NonNull ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder>... delegates) {
        if (delegates == null || clazz == null) {
            throw new NullPointerException("class or delegates should not be NULL!");
        }
        for (ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder> delegate : delegates) {
            this.delegates.put(this.delegates.size(), delegate);
        }
        typeMap.put(clazz, new ItemTypeHolder<>(clazz, converter, delegates));
    }

    public int getDelegateCount() {
        return delegates.size();
    }

    /**
     * 获取对应的item Type, 为保证ItemType的唯一性, 采用delegates列表中的下标
     *
     * @param item     数据项
     * @param position 下标
     * @return 布局类型
     */
    public int getItemViewType(final Object item, final int position) {
        Class<?> itemClass = item.getClass();
        ItemTypeHolder<?> itemTypeHolder = getItemTypeHolder(itemClass);
        if (itemTypeHolder != null) {
            ItemViewDelegate<?, ? extends RecyclerView.ViewHolder> delegate = itemTypeHolder.getItemViewDelegate(item, position);
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
     * @param viewType 布局类型
     * @return 布局代理
     */
    public ItemViewDelegate<?, ? extends RecyclerView.ViewHolder> getItemViewDelegate(int viewType) {
        return delegates.get(viewType);
    }

    @Nullable
    private ItemTypeHolder<?> getItemTypeHolder(Class<?> clazz) {
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
