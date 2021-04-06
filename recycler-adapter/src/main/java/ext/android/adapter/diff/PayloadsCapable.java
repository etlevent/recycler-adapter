package ext.android.adapter.diff;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by roothost on 2018/3/20.
 */

public interface PayloadsCapable<T, VH extends RecyclerView.ViewHolder> {
    void payloads(VH holder, int position, List<T> payloads);
}
