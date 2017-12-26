package cherry.android.recycler;

/**
 * Created by Administrator on 2017/6/19.
 */

public interface ViewConverter<T> {
    //    Class<? extends ItemViewDelegate<? extends T, ? extends RecyclerView.ViewHolder>> convert(T t, int position);
    Class<? extends ItemViewDelegate> convert(T t, int position);
}
