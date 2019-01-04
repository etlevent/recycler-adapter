package ext.android.adapter.diff;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import ext.android.adapter.delegate.BaseItemViewDelegate;

/**
 * Created by roothost on 2018/3/20.
 */

public abstract class PayloadsItemViewDelegate<T, P, VH extends RecyclerView.ViewHolder> extends BaseItemViewDelegate<T, VH>
        implements PayloadsCapable<P, VH> {

    public PayloadsItemViewDelegate(int layoutId) {
        super(layoutId);
    }

    @Override
    public void payloads(VH holder, int position, List<P> payloads) {
        // default do nothing
    }
}
