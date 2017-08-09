package cherry.android.recycler;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;

import java.util.Map;

/**
 * Created by Administrator on 2017/6/19.
 */

/*public*/ class ItemViewDelegateManager {
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
                                                                    @NonNull ItemViewDelegate<T, VH> delegate) {
        if (delegate == null || clazz == null)
            throw new NullPointerException("class or delegate should not be NULL!");
        delegates.put(delegates.size(), delegate);
        typeMap.put(clazz, new ItemTypeHolder(clazz, delegate));
    }

    /**
     * 添加Class - items映射, 一对多映射关系
     *
     * @param clazz
     * @param chooser
     * @param delegates
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    protected <T> void addDelegate(@NonNull Class<? extends T> clazz,
                                   @NonNull ViewChooser<T> chooser,
                                   @NonNull ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder>... delegates) {
        if (delegates == null || clazz == null)
            throw new NullPointerException("class or delegates should not be NULL!");
        for (ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder> delegate : delegates) {
            this.delegates.put(this.delegates.size(), delegate);
        }
        typeMap.put(clazz, new ItemTypeHolder(clazz, chooser, delegates));
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
    public int getItemViewType(@NonNull final Object item, final int position) {
        Class itemClass = item.getClass();
        ItemViewDelegate delegate;
        for (Map.Entry<Class<?>, ItemTypeHolder<?>> entry : typeMap.entrySet()) {
            if (itemClass.equals(entry.getValue().getItemClass())) {
                delegate = entry.getValue().getItemViewDelegate(item, position);
                return delegates.indexOfValue(delegate);
            }
        }
        for (Map.Entry<Class<?>, ItemTypeHolder<?>> entry : typeMap.entrySet()) {
            if (entry.getValue().getItemClass().isAssignableFrom(itemClass)) {
                delegate = entry.getValue().getItemViewDelegate(item, position);
                return delegates.indexOfValue(delegate);
            }
        }
        throw new IllegalArgumentException("cannot get A itemType with item: ["
                + item + "], position=[" + position + "]");
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
}
