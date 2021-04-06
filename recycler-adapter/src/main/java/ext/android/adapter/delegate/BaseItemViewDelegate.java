package ext.android.adapter.delegate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ext.android.adapter.ViewHolderHelper;

/**
 * Created by ROOT on 2017/8/7.
 */

public abstract class BaseItemViewDelegate<T, VH extends RecyclerView.ViewHolder> implements ItemViewDelegate<T, VH> {

    @LayoutRes
    private final int mLayoutId;
    private Class<? extends VH> mHolderClass;

    public BaseItemViewDelegate(@LayoutRes int layoutId) {
        this.mLayoutId = layoutId;
    }


    @NonNull
    @Override
    public final VH createViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View itemView = inflater.inflate(this.mLayoutId, parent, false);
        return createViewHolder(itemView);
    }

    @SuppressWarnings("unchecked")
    private VH createViewHolder(View itemView) {
        if (mHolderClass == null) {
            mHolderClass = ViewHolderHelper.getViewHolderClassFromDelegate((Class<? extends ItemViewDelegate<T, ? extends VH>>) getClass());
        }
        return ViewHolderHelper.createViewHolder(mHolderClass, itemView);
    }
}
