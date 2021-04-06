package ext.android.adapter;

import android.view.View;

import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 2017/6/6.
 */

public class ViewHolder extends RecyclerView.ViewHolder {

    private final SparseArrayCompat<View> mHolderViews;

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
