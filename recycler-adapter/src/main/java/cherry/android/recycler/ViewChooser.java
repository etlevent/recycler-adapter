package cherry.android.recycler;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Administrator on 2017/6/19.
 */

public interface ViewChooser<T> {
    Class<? extends ItemViewDelegate<T, ? extends RecyclerView.ViewHolder>> choose(T t, int position);
}
