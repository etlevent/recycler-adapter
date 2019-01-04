package cherry.android.recycler.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ext.android.adapter.CommonAdapter;
import ext.android.adapter.RecyclerAdapter;
import ext.android.adapter.ViewHolder;
import cherry.android.recycler.sample.linear.LinearActivity;

public class MainActivity extends RecyclerActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(getDefaultItemDecoration());

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
                }
            }
        });
        mRecyclerView.setAdapter(adapter);

    }
}
