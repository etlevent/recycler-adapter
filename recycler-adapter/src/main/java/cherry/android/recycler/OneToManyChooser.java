package cherry.android.recycler;

/**
 * Created by Administrator on 2017/6/19.
 */

public interface OneToManyChooser<T> {
    void to(ViewChooser<T> chooser);
}
