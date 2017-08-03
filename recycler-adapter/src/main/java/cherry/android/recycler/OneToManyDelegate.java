package cherry.android.recycler;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Administrator on 2017/6/19.
 */

public interface OneToManyDelegate<T> {
    OneToManyChooser bindDelegate(ItemViewDelegate<T, ? extends RecyclerView.ViewHolder>... delegates);
}
