package cherry.android.recycler.sample.grid;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cherry.android.recycler.sample.RecyclerActivity;
import ext.android.adapter.CommonAdapter;
import ext.android.adapter.RecyclerAdapter;
import ext.android.adapter.ViewHolder;

public class GridActivity extends RecyclerActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            items.add("Grid " + i);
        }
        RecyclerAdapter adapter = new CommonAdapter<String, ViewHolder>(items, android.R.layout.simple_list_item_1) {

            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                TextView textView = holder.findView(android.R.id.text1);
                textView.setText(s);
            }
        };
        mRecyclerView.setAdapter(adapter);
    }
}
