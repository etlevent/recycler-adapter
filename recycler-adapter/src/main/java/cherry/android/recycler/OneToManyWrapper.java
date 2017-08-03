package cherry.android.recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Administrator on 2017/6/19.
 */

public class OneToManyWrapper<T> implements OneToManyDelegate<T>, OneToManyChooser<T> {

    private Class<? extends T> clazz;
    private ItemViewDelegateManager manager;
    private ItemViewDelegate<T, ? extends RecyclerView.ViewHolder>[] delegates;
    private ViewChooser<T> chooser;

    public OneToManyWrapper(@NonNull Class<? extends T> clazz,
                            @NonNull ItemViewDelegateManager manager) {
        this.clazz = clazz;
        this.manager = manager;
    }

    @Override
    public void to(ViewChooser<T> chooser) {
        this.chooser = chooser;
        manager.addDelegate(clazz, this.chooser, this.delegates);
    }

    @Override
    public OneToManyChooser bindDelegate(ItemViewDelegate<T, ? extends RecyclerView.ViewHolder>... delegates) {
        this.delegates = delegates;
        return this;
    }
}
