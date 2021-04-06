package cherry.android.recycler.sample.delegate;

import androidx.annotation.NonNull;
import android.widget.TextView;

import cherry.android.recycler.sample.model.Child;
import ext.android.adapter.ViewHolder;
import ext.android.adapter.delegate.BaseItemViewDelegate;

public class ChildDelegate extends BaseItemViewDelegate<Child, ViewHolder> {
    public ChildDelegate() {
        super(android.R.layout.simple_list_item_2);
    }

    @Override
    public void convert(@NonNull ViewHolder holder, Child child, int position) {
        TextView tv = holder.findView(android.R.id.text1);
        tv.setText(child.getName());
        tv = holder.findView(android.R.id.text2);
        tv.setText(child.getAge() + "");
    }
}
