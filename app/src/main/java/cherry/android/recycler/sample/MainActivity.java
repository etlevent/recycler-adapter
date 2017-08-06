package cherry.android.recycler.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cherry.android.recycler.CommonAdapter;
import cherry.android.recycler.RecyclerAdapter;
import cherry.android.recycler.ViewHolder;
import cherry.android.recycler.wrapper.HeaderAndFooterWrapper;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommonAdapter mAdapter;
    private HeaderAndFooterWrapper mWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        mAdapter = new CommonAdapter<String, ViewHolder>(android.R.layout.activity_list_item) {

            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                TextView textView = holder.findView(android.R.id.text1);
                textView.setText(s);
            }

//            @Override
//            protected ViewHolder createDefaultViewHolder(View itemView) {
//                return new ViewHolder(itemView);
//            }
        };
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
                Log.d("Test", "postion = " + position);
            }
        });
    }
}
