package cherry.android.recycler.sample.delegate;

import androidx.annotation.NonNull;
import android.widget.TextView;

import ext.android.adapter.delegate.BaseItemViewDelegate;
import ext.android.adapter.ViewHolder;
import cherry.android.recycler.sample.model.Foo;
import cherry.android.recycler.sample.model.Foo2;

/**
 * Created by ROOT on 2017/8/9.
 */

public class SimpleDelegate2 extends BaseItemViewDelegate<Foo<Foo2>, ViewHolder> {

    public SimpleDelegate2() {
        super(android.R.layout.simple_list_item_2);
    }

    @Override
    public void convert(@NonNull ViewHolder holder, Foo<Foo2> foo2Foo, int position) {
        TextView tv = holder.findView(android.R.id.text1);
        tv.setText("simple item 2");
        tv = holder.findView(android.R.id.text2);
        tv.setText(foo2Foo.getBody().description());
    }
}
