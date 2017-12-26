package cherry.android.recycler;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Administrator on 2017/6/6.
 */
public abstract class CommonAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerAdapter {

    public CommonAdapter(List<T> data, @LayoutRes int itemLayoutId) {
        super(data);
        addDelegateWithId(itemLayoutId);
    }

    public CommonAdapter(@LayoutRes int itemLayoutId) {
        super();
        addDelegateWithId(itemLayoutId);
    }

    @SuppressWarnings("unchecked")
    private void addDelegateWithId(@LayoutRes final int itemLayoutId) {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Class<T> clazz = (Class<T>) params[0];
        final Class<VH> holderClazz = (Class<VH>) params[1];
        addDelegate(clazz, new ItemViewDelegate<T, VH>() {
            @NonNull
            @Override
            public VH createViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
                View itemView = inflater.inflate(itemLayoutId, parent, false);
                return ViewHolderHelper.createViewHolder(holderClazz, itemView);
            }

            @Override
            public void convert(@NonNull VH holder, T t, int position) {
                CommonAdapter.this.convert(holder, t, position);
            }
        });
    }

    protected abstract void convert(VH holder, T t, int position);
}
