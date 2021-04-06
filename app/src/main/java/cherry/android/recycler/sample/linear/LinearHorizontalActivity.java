package cherry.android.recycler.sample.linear;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cherry.android.recycler.sample.R;
import cherry.android.recycler.sample.RecyclerActivity;
import ext.android.adapter.RecyclerAdapter;
import ext.android.adapter.ViewHolder;
import ext.android.adapter.delegate.BaseItemViewDelegate;

public class LinearHorizontalActivity extends RecyclerActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List list = new ArrayList();
        for (int i = 0; i < 50; i++) {
            if (nextInt(2) == 0) {
                list.add("string item : " + i);
            } else {
                list.add(new Item("item: " + i));
            }
        }
        RecyclerAdapter adapter = new RecyclerAdapter(list);
        adapter.addDelegate(String.class, new StringItemDelegate());
        adapter.addDelegate(Item.class, new ItemDelegate());
        mRecyclerView.setAdapter(adapter);
    }

    private static class StringItemDelegate extends BaseItemViewDelegate<String, ViewHolder> {

        public StringItemDelegate() {
            super(R.layout.item_linear_horizontal_2);
        }

        @Override
        public void convert(@NonNull ViewHolder holder, String s, int position) {
            TextView textView = holder.findView(R.id.tv);
            textView.setText(s);
        }
    }

    private static class ItemDelegate extends BaseItemViewDelegate<Item, ViewHolder> {

        public ItemDelegate() {
            super(R.layout.item_linear_horizontal_1);
        }

        @Override
        public void convert(@NonNull ViewHolder holder, Item item, int position) {
            TextView textView = holder.findView(R.id.tv);
            textView.setText(item.name);
        }
    }

    private static class Item {
        String name;

        public Item(String name) {
            this.name = name;
        }
    }
}
