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

    private Constructor<VH> constructor;

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
//                return CommonAdapter.this.createDefaultViewHolder(itemView);
                return createDefaultViewHolder(holderClazz, itemView);
            }

            @Override
            public void convert(@NonNull VH holder, T t, int position) {
                CommonAdapter.this.convert(holder, t, position);
            }
        });
    }

    protected abstract void convert(VH holder, T t, int position);

//    protected abstract VH createDefaultViewHolder(View itemView);

    private VH createDefaultViewHolder(Class<VH> holder, View itemView) {
        if (constructor == null)
            constructor = getHolderConstructor(holder);
        try {
            return constructor.newInstance(itemView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("cant create instance for class: " + holder);
    }

    private static <VH> Constructor<VH> getHolderConstructor(Class<VH> clazz) {
        try {
            return (Constructor) clazz.getConstructor(View.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("can't get construct with View.class for class: " + clazz);
        }
    }
}
