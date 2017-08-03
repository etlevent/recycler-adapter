package cherry.android.recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Administrator on 2017/6/6.
 */

public abstract class CommonAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerAdapter {

    public CommonAdapter(List<T> data, int itemLayoutId) {
        super(data);
        addDelegateWithId(itemLayoutId);
    }

    public CommonAdapter(int itemLayoutId) {
        super();
        addDelegateWithId(itemLayoutId);
    }

    private void addDelegateWithId(final int itemLayoutId) {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Class<T> clazz = (Class<T>) params[0];
        addDelegate(clazz, new ItemViewDelegate<T, VH>() {
            @NonNull
            @Override
            public VH createViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
                View itemView = inflater.inflate(itemLayoutId, parent, false);
                return CommonAdapter.this.createDefaultViewHolder(itemView);
            }

            @Override
            public void convert(VH holder, T t, int position) {
                CommonAdapter.this.convert(holder, t, position);
            }
        });
    }

    protected abstract void convert(VH holder, T t, int position);

    protected abstract VH createDefaultViewHolder(View itemView);
}
