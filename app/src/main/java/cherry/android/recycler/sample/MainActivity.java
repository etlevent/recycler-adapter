package cherry.android.recycler.sample;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cherry.android.recycler.sample.grid.GridActivity;
import cherry.android.recycler.sample.linear.LinearActivity;
import ext.android.adapter.CommonAdapter;
import ext.android.adapter.RecyclerAdapter;
import ext.android.adapter.ViewHolder;

public class MainActivity extends RecyclerActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        final List<String> items = new ArrayList<>();
        items.add("Linear");
        items.add("Grid");
        items.add("StaggeredGrid");
        RecyclerAdapter adapter = new CommonAdapter<String, ViewHolder>(items, android.R.layout.simple_list_item_1) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                TextView textView = holder.findView(android.R.id.text1);
                textView.setText(s);
            }
        };
        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, RecyclerView.ViewHolder holder, int position) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, LinearActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this, GridActivity.class));
                        break;
                }
            }
        });
        adapter.setOnItemLongClickListener(new RecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View itemView, RecyclerView.ViewHolder holder, int position) {
                Toast.makeText(MainActivity.this, "onItemLongClick [" + position + "]", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mRecyclerView.setAdapter(adapter);

    }
}
