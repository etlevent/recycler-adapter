package cherry.android.recycler.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cherry.android.recycler.CommonAdapter;
import cherry.android.recycler.ViewHolder;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        CommonAdapter adapter = new CommonAdapter<String, ViewHolder>(android.R.layout.activity_list_item) {

            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                TextView textView = holder.findView(android.R.id.text1);
                textView.setText(s);
            }

            @Override
            protected ViewHolder createDefaultViewHolder(View itemView) {
                return new ViewHolder(itemView);
            }
        };
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("item:" + i);
        }
        adapter.setItems(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
