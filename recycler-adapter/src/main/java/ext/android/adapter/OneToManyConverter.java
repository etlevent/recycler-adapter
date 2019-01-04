package ext.android.adapter;

/**
 * Created by Administrator on 2017/6/19.
 */

public interface OneToManyConverter<T> {
    void to(ItemViewDelegateConverter<T> converter);
}
