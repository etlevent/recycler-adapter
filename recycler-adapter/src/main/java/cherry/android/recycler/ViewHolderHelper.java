package cherry.android.recycler;

import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * Created by ROOT on 2017/9/21.
 */

/*package-private*/ final class ViewHolderHelper {
    private ViewHolderHelper() {
        throw new AssertionError("no instance.");
    }

    private static final Map<Class<? extends RecyclerView.ViewHolder>,
            Constructor<? extends RecyclerView.ViewHolder>> CONSTRUCTOR_MAP = new ArrayMap<>();

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
            if (constructor != null)
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
}
