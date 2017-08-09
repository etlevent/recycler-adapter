package cherry.android.recycler;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by ROOT on 2017/8/7.
 */

public abstract class BaseItemViewDelegate<T, VH extends RecyclerView.ViewHolder> implements ItemViewDelegate<T, VH> {

    @LayoutRes
    private final int mLayoutId;
    private Constructor<VH> mConstructor;

    public BaseItemViewDelegate(@LayoutRes int layoutId) {
        this.mLayoutId = layoutId;
    }

    @NonNull
    public final VH createViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View itemView = inflater.inflate(this.mLayoutId, parent, false);
        return createViewHolder(itemView);
    }

    private VH createViewHolder(View itemView) {
        if (mConstructor == null) {
            Type type = getClass().getGenericSuperclass();
            Type[] params = ((ParameterizedType) type).getActualTypeArguments();
            @SuppressWarnings("unchecked") Class<VH> clazz = (Class<VH>) params[1];
            mConstructor = getHolderConstructor(clazz);
        }
        try {
            return mConstructor.newInstance(itemView);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("cannot create holder instance by view");
    }

    private static <VH> Constructor<VH> getHolderConstructor(Class<VH> clazz) {
        try {
            return clazz.getConstructor(View.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("cannot find constructor with parameter View.class: " + clazz);
    }
}
