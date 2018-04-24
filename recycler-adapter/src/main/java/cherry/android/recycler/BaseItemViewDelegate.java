package cherry.android.recycler;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by ROOT on 2017/8/7.
 */

public abstract class BaseItemViewDelegate<T, VH extends RecyclerView.ViewHolder> implements ItemViewDelegate<T, VH> {

    @LayoutRes
    private final int mLayoutId;
    private Class<VH> mHolderClass;

    public BaseItemViewDelegate(@LayoutRes int layoutId) {
        this.mLayoutId = layoutId;
    }

    private static <H> Class<H> findHolderClass(Class clazz) {
        Type type = clazz.getGenericSuperclass();
        Type[] params = ((ParameterizedType) type).getActualTypeArguments();
        if (params.length < 2) {
            if (!ItemViewDelegate.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException("cannot find Holder CLASS");
            }
            return findHolderClass(clazz.getSuperclass());
        }
        for (Type param : params) {
            if (Class.class.isAssignableFrom(param.getClass())) {
                Class pClass = (Class) param;
                if (RecyclerView.ViewHolder.class.isAssignableFrom(pClass)) {
                    return pClass;
                }
            }
        }
        final Class superClass = clazz.getSuperclass();
        if (!ItemViewDelegate.class.isAssignableFrom(superClass)) {
            throw new IllegalArgumentException("cannot find Holder CLASS");
        }
        return findHolderClass(superClass);
    }

    @NonNull
    @Override
    public final VH createViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View itemView = inflater.inflate(this.mLayoutId, parent, false);
        return createViewHolder(itemView);
    }

    private VH createViewHolder(View itemView) {
        if (mHolderClass == null) {
            mHolderClass = findHolderClass(getClass());
        }
        return ViewHolderHelper.createViewHolder(mHolderClass, itemView);
    }
}
