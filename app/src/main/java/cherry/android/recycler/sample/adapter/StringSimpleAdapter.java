package cherry.android.recycler.sample.adapter;

import android.view.View;
import android.widget.TextView;

import java.util.List;

import ext.android.adapter.CommonAdapter;
import ext.android.adapter.ViewHolder;

public class StringSimpleAdapter extends CommonAdapter<String, StringSimpleAdapter.SimpleViewHolder> {
    public StringSimpleAdapter() {
        super(android.R.layout.simple_list_item_1);
    }

    public StringSimpleAdapter(List<String> data) {
        super(data, android.R.layout.simple_list_item_1);
    }


    @Override
    protected void convert(SimpleViewHolder holder, String s, int position) {
        TextView textView = holder.findView(android.R.id.text1);
        textView.setText(s);
    }

    static class SimpleViewHolder extends ViewHolder {

        SimpleViewHolder(View itemView) {
            super(itemView);
        }
    }
}
