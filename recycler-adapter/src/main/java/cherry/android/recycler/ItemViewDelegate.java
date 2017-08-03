package cherry.android.recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017/6/6.
 */

public interface ItemViewDelegate<T, VH extends RecyclerView.ViewHolder> {

    @NonNull
    VH createViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    void convert(@NonNull VH holder, T t, int position);
}
