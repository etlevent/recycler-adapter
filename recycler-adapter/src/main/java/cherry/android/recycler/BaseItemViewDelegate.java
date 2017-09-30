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

    public BaseItemViewDelegate(@LayoutRes int layoutId) {
        this.mLayoutId = layoutId;
    }

    @NonNull
    public final VH createViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View itemView = inflater.inflate(this.mLayoutId, parent, false);
        return createViewHolder(itemView);
    }

    private Class<VH> mHolderClass;

    private VH createViewHolder(View itemView) {
        if (mHolderClass == null) {
            mHolderClass = findHolderClass(getClass());
        }
        return ViewHolderHelper.createViewHolder(mHolderClass, itemView);
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
        return (Class<H>) params[1];
    }
}
