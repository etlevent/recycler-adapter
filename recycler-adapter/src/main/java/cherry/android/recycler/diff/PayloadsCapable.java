package cherry.android.recycler.diff;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by roothost on 2018/3/20.
 */

public interface PayloadsCapable<P, VH extends RecyclerView.ViewHolder> {
    void payloads(VH holder, int position, List<P> payloads);
}
