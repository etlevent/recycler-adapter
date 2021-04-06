package cherry.android.recycler.sample.delegate;

import androidx.annotation.NonNull;
import android.widget.TextView;

import cherry.android.recycler.sample.model.Foo1;
import ext.android.adapter.ViewHolder;
import ext.android.adapter.delegate.BaseItemViewDelegate;

public class Foo1Delegate extends BaseItemViewDelegate<Foo1, ViewHolder> {
    public Foo1Delegate() {
        super(android.R.layout.simple_list_item_1);
    }

    @Override
    public void convert(@NonNull ViewHolder holder, Foo1 foo1, int position) {
        TextView tv = holder.findView(android.R.id.text1);
        tv.setText(foo1.description());
    }
}
