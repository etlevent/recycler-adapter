package cherry.android.recycler.sample.linear;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cherry.android.recycler.sample.R;
import cherry.android.recycler.sample.model.IdModel;
import ext.android.adapter.CommonAdapter;
import ext.android.adapter.ItemViewDelegateConverter;
import ext.android.adapter.RecyclerAdapter;
import ext.android.adapter.ViewHolder;
import ext.android.adapter.delegate.ItemViewDelegate;
import ext.android.adapter.diff.PayloadsItemViewDelegate;
import ext.android.adapter.wrapper.HeaderAndFooterWrapper;

public class LinearVerticalActivity extends AppCompatActivity {

    private RecyclerAdapter mAdapter;
    private HeaderAndFooterWrapper mWrapper;
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAdapter.setList((List<?>) msg.obj);
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear);
        final RecyclerView recyclerView = findViewById(R.id.recycler);
        mAdapter = new CommonAdapter<String, ViewHolder>(android.R.layout.activity_list_item) {

            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                TextView textView = holder.findView(android.R.id.text1);
                textView.setText(s);
            }
        };
        mAdapter = new RecyclerAdapter();
        mAdapter.addDelegate(IdModel.class).bindDelegate(new ItemViewDelegate1(), new ItemViewDelegate2())
                .to(new ItemViewDelegateConverter<IdModel>() {
                    @Override
                    public Class<? extends ItemViewDelegate<IdModel, ? extends RecyclerView.ViewHolder>> convert(IdModel idModel, int position) {
                        return position % 2 == 0 ? ItemViewDelegate1.class : ItemViewDelegate2.class;
                    }
                });
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<IdModel> list = new ArrayList<>();
                for (int i = 0; i < 1000L; i++) {
                    list.add(new IdModel(i, "item:" + i));
                }
                mHandler.obtainMessage(0, list).sendToTarget();
            }
        }).start();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWrapper = new HeaderAndFooterWrapper(mAdapter);
        mWrapper.addHeaderView(LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1, recyclerView, false));
        mWrapper.addHeaderView(LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1, recyclerView, false));
        mWrapper.addFooterView(LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1, recyclerView, false));
        mWrapper.addFooterView(LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1, recyclerView, false));
        recyclerView.setAdapter(mWrapper);
        mAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, RecyclerView.ViewHolder holder, int position) {
                startActivity(new Intent(LinearVerticalActivity.this, position % 2 == 0 ? SecondActivity.class : ThirdActivity.class));
            }
        });
        final Random random = new Random();
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                List<IdModel> list = new ArrayList(mAdapter.getList());
                int index = random.nextInt(list.size());
                list.add(index, new IdModel(list.size(), "add " + index));
                index = random.nextInt(list.size());
                IdModel model = list.get(index);
                IdModel newModel = new IdModel(model.getId(), "payloads!!!!!!!" + index);
                list.set(index, newModel);

                model = list.get(0);
                newModel = new IdModel(model.getId(), "payloads!!!!!!!" + index);
                list.set(0, newModel);

                mAdapter.setList(list);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private static class ItemViewDelegate1 extends PayloadsItemViewDelegate<IdModel, String, ViewHolder> {
        ItemViewDelegate1() {
            super(android.R.layout.simple_list_item_1);
        }

        @Override
        public void convert(@NonNull ViewHolder holder, IdModel idModel, int position) {
            TextView textView = holder.findView(android.R.id.text1);
            textView.setText("item1 position = " + position + ", s=" + idModel.getContent());
        }

        @Override
        public void payloads(ViewHolder holder, int position, List<String> payloads) {
            Log.e("RecyclerAdapter", "payloads=" + payloads);
            TextView textView = holder.findView(android.R.id.text1);
            textView.setText(payloads.get(0));
        }
    }

    private static class ItemViewDelegate2 extends PayloadsItemViewDelegate<IdModel, String, ViewHolder> {

        ItemViewDelegate2() {
            super(android.R.layout.simple_list_item_2);
        }

        @Override
        public void convert(@NonNull ViewHolder holder, IdModel idModel, int position) {
            TextView tv = holder.findView(android.R.id.text1);
            tv.setText("item2");
            tv = holder.findView(android.R.id.text2);
            tv.setText("position=" + position + ", s=" + idModel.getContent());
        }

        @Override
        public void payloads(ViewHolder holder, int position, List<String> payloads) {
            TextView textView = holder.findView(android.R.id.text2);
            textView.setText(payloads.get(0));
        }
    }
}
