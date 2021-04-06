package cherry.android.recycler.sample.delegate;

import androidx.annotation.NonNull;
import android.widget.TextView;

import ext.android.adapter.delegate.BaseItemViewDelegate;
import ext.android.adapter.ViewHolder;
import cherry.android.recycler.sample.model.Foo;
import cherry.android.recycler.sample.model.Foo1;

/**
 * Created by ROOT on 2017/8/9.
 */

public class SimpleDelegate1 extends BaseItemViewDelegate<Foo<Foo1>, ViewHolder> {
    public SimpleDelegate1() {
        super(android.R.layout.simple_list_item_1);
    }

    @Override
    public void convert(@NonNull ViewHolder holder, Foo<Foo1> foo1Foo, int position) {
        TextView tv = holder.findView(android.R.id.text1);
        tv.setText(foo1Foo.getBody().description());
    }
}
