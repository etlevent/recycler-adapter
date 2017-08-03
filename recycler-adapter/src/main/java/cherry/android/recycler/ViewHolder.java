package cherry.android.recycler;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/6/6.
 */

public class ViewHolder extends RecyclerView.ViewHolder {

    private SparseArrayCompat<View> mHolderViews;

    public ViewHolder(View itemView) {
        super(itemView);
        mHolderViews = new SparseArrayCompat<>(1);
    }

    public <T extends View> T findView(int id) {
        View view = mHolderViews.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            mHolderViews.put(id, view);
        }
        return (T) view;
    }
}
