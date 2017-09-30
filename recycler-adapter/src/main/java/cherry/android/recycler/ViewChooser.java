package cherry.android.recycler;

/**
 * Created by Administrator on 2017/6/19.
 */

public interface ViewChooser<T> {
    //    Class<? extends ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder>> choose(T t, int position);
    Class<? extends ItemViewDelegate> choose(T t, int position);
}
