package cherry.android.recycler.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cherry.android.recycler.BaseItemViewDelegate;
import cherry.android.recycler.CommonAdapter;
import cherry.android.recycler.ItemViewDelegate;
import cherry.android.recycler.RecyclerAdapter;
import cherry.android.recycler.ViewConverter;
import cherry.android.recycler.ViewHolder;
import cherry.android.recycler.wrapper.HeaderAndFooterWrapper;

public class MainActivity extends AppCompatActivity {

    private RecyclerAdapter mAdapter;
    private HeaderAndFooterWrapper mWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        mAdapter = new CommonAdapter<String, ViewHolder>(android.R.layout.activity_list_item) {

            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                TextView textView = holder.findView(android.R.id.text1);
                textView.setText(s);
            }
        };
        mAdapter = new RecyclerAdapter();
        mAdapter.addDelegate(String.class).bindDelegate(new ItemViewDelegate1(), new ItemViewDelegate2())
                .to(new ViewConverter<String>() {
                    @Override
                    public Class<? extends ItemViewDelegate<String, ? extends RecyclerView.ViewHolder>> convert(String s, int position) {
                        return position % 2 == 0 ? ItemViewDelegate1.class : ItemViewDelegate2.class;
                    }
                });
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            list.add("item:" + i);
        }
        mAdapter.setItems(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWrapper = new HeaderAndFooterWrapper(mAdapter);
        mWrapper.addHeaderView(LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1, recyclerView, false));
        recyclerView.setAdapter(mWrapper);
        mAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, RecyclerView.ViewHolder holder, int position) {
                Log.d("Test", "position = " + position);
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });
    }

    private static class ItemViewDelegate1 extends BaseItemViewDelegate<String, ViewHolder> {
        public ItemViewDelegate1() {
            super(android.R.layout.simple_list_item_1);
        }

        @Override
        public void convert(@NonNull ViewHolder holder, String s, int position) {
            TextView textView = holder.findView(android.R.id.text1);
            textView.setText("item1 position = " + position);
        }
    }

    private static class ItemViewDelegate2 extends BaseItemViewDelegate<String, ViewHolder> {

        public ItemViewDelegate2() {
            super(android.R.layout.simple_list_item_2);
        }

        @Override
        public void convert(@NonNull ViewHolder holder, String s, int position) {
            TextView tv = holder.findView(android.R.id.text1);
            tv.setText("item2");
            tv = holder.findView(android.R.id.text2);
            tv.setText("position=" + position);
        }
    }
}
