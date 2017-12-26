package cherry.android.recycler;

/**
 * Created by Administrator on 2017/6/19.
 */

public interface OneToManyConverter<T> {
    void to(ViewConverter<T> converter);
}
