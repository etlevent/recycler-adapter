package cherry.android.recycler.sample.linear;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cherry.android.recycler.CommonAdapter;
import cherry.android.recycler.RecyclerAdapter;
import cherry.android.recycler.ViewHolder;
import cherry.android.recycler.sample.RecyclerActivity;

public class LinearActivity extends RecyclerActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<String> items = new ArrayList<>();
        items.add("HORIZONTAL");
        items.add("VERTICAL");
        items.add("HORIZONTAL with Header and Footer");
        items.add("VERTICAL with Header and Footer");
        items.add("VERTICAL with Loading More");
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
                        startActivity(new Intent(LinearActivity.this, LinearHorizontalActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(LinearActivity.this, LinearVerticalActivity.class));
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        startActivity(new Intent(LinearActivity.this, LinearLoadingMoreActivity.class));
                        break;
                }
            }
        });
        mRecyclerView.setAdapter(adapter);
    }
}
