package cherry.android.recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Administrator on 2017/6/19.
 */

/*package-private*/ class OneToManyWrapper<T> implements OneToManyDelegate<T>, OneToManyConverter<T> {

    private final Class<? extends T> clazz;
    private final ItemViewDelegateManager manager;
    private ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder>[] delegates;

    public OneToManyWrapper(@NonNull Class<? extends T> clazz,
                            @NonNull ItemViewDelegateManager manager) {
        this.clazz = clazz;
        this.manager = manager;
    }

    @Override
    public void to(ViewConverter<T> converter) {
        if (this.delegates == null) {
            throw new IllegalArgumentException("bindDelegate first with NonNull " + ItemViewDelegate.class.getSimpleName());
        }
        manager.addDelegate(clazz, converter, this.delegates);
    }

    @SafeVarargs
    @Override
    public final OneToManyConverter<T> bindDelegate(ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder>... delegates) {
        this.delegates = delegates;
        return this;
    }
}
