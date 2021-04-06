package cherry.android.recycler.sample.delegate;

import androidx.annotation.NonNull;
import android.widget.TextView;

import cherry.android.recycler.sample.model.Foo3;
import ext.android.adapter.ViewHolder;
import ext.android.adapter.delegate.BaseItemViewDelegate;

public class Foo3Delegate extends BaseItemViewDelegate<Foo3, ViewHolder> {
    public Foo3Delegate() {
        super(android.R.layout.simple_list_item_2);
    }

    @Override
    public void convert(@NonNull ViewHolder holder, Foo3 foo3, int position) {
        TextView tv = holder.findView(android.R.id.text1);
        tv.setText("foo item 3");
        tv = holder.findView(android.R.id.text2);
        tv.setText(foo3.description());
    }
}
