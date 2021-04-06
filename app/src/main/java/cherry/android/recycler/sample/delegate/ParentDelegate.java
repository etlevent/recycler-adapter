package cherry.android.recycler.sample.delegate;

import androidx.annotation.NonNull;
import android.widget.TextView;

import cherry.android.recycler.sample.model.Parent;
import ext.android.adapter.ViewHolder;
import ext.android.adapter.delegate.BaseItemViewDelegate;

public class ParentDelegate extends BaseItemViewDelegate<Parent, ViewHolder> {
    public ParentDelegate() {
        super(android.R.layout.simple_list_item_1);
    }

    @Override
    public void convert(@NonNull ViewHolder holder, Parent parent, int position) {
        TextView tv = holder.findView(android.R.id.text1);
        tv.setText(parent.getName());
    }
}
