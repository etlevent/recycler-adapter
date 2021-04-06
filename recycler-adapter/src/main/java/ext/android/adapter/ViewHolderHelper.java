package ext.android.adapter;

import android.view.View;

import androidx.annotation.RestrictTo;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import ext.android.adapter.delegate.ItemViewDelegate;

/**
 * Created by ROOT on 2017/9/21.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class ViewHolderHelper {
    private static final Map<Class<? extends RecyclerView.ViewHolder>,
            Constructor<? extends RecyclerView.ViewHolder>> CONSTRUCTOR_MAP = new ArrayMap<>();

    private ViewHolderHelper() {
        throw new AssertionError("no instance.");
    }

    @SuppressWarnings("unchecked")
    public static <T extends RecyclerView.ViewHolder> T createViewHolder(Class<T> holder, View itemView) {
        Constructor<? extends RecyclerView.ViewHolder> constructor = CONSTRUCTOR_MAP.get(holder);
        if (constructor == null) {
            constructor = createConstructor(holder);
            CONSTRUCTOR_MAP.put(holder, constructor);
        }
        try {
            constructor.setAccessible(true);
            return (T) constructor.newInstance(itemView);
        } catch (Exception e) {
            throw new IllegalArgumentException("cant create instance for class: " + holder, e);
        } finally {
            constructor.setAccessible(false);
        }

    }

    private static <T extends RecyclerView.ViewHolder> Constructor<T> createConstructor(Class<T> clazz) {
        try {
            return clazz.getConstructor(View.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("can't get construct in class: " + clazz + "(View itemView)", e);
        }
    }


    @SuppressWarnings("unchecked")
    public static <T, VH extends RecyclerView.ViewHolder> Class<VH> getViewHolderClassFromDelegate(Class<? extends ItemViewDelegate<T, ? extends VH>> itemViewDelegateClazz) {
        Type type = itemViewDelegateClazz.getGenericSuperclass();
        if (type != null && ParameterizedType.class.isAssignableFrom(type.getClass())) {
            Type[] params = ((ParameterizedType) type).getActualTypeArguments();
            if (params.length < 2) {
                if (!ItemViewDelegate.class.isAssignableFrom(itemViewDelegateClazz)) {
                    throw new IllegalArgumentException("cannot find class ViewHolder or extends ViewHolder");
                }
                final Class<?> superclass = itemViewDelegateClazz.getSuperclass();
                if (superclass != null && ItemViewDelegate.class.isAssignableFrom(superclass))
                    return getViewHolderClassFromDelegate((Class<? extends ItemViewDelegate<Object, ? extends VH>>) superclass);
            }
            for (Type param : params) {
                if (Class.class.isAssignableFrom(param.getClass())) {
                    Class<?> pClass = (Class<?>) param;
                    if (RecyclerView.ViewHolder.class.isAssignableFrom(pClass)) {
                        return (Class<VH>) pClass;
                    }
                }
            }
        }
        final Class<?> superclass = itemViewDelegateClazz.getSuperclass();
        if (superclass == null)
            throw new IllegalArgumentException("cannot find class ViewHolder or extends ViewHolder");
        if (!ItemViewDelegate.class.isAssignableFrom(superclass)) {
            throw new IllegalArgumentException("cannot find class ViewHolder or extends ViewHolder");
        }
        return getViewHolderClassFromDelegate((Class<? extends ItemViewDelegate<Object, ? extends VH>>) superclass);
    }

}
