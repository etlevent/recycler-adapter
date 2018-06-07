package cherry.android.recycler.sample.linear;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.util.Random;

import cherry.android.recycler.CommonAdapter;
import cherry.android.recycler.ItemViewDelegate;
import cherry.android.recycler.RecyclerAdapter;
import cherry.android.recycler.ViewConverter;
import cherry.android.recycler.ViewHolder;
import cherry.android.recycler.diff.DiffCapable;
import cherry.android.recycler.diff.PayloadsItemViewDelegate;
import cherry.android.recycler.sample.R;
import cherry.android.recycler.sample.model.IdModel;
import cherry.android.recycler.wrapper.HeaderAndFooterWrapper;

public class LinearVerticalActivity extends AppCompatActivity {

    private RecyclerAdapter mAdapter;
    private HeaderAndFooterWrapper mWrapper;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAdapter.setItems((List<?>) msg.obj);
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
                .to(new ViewConverter<IdModel>() {
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
                Log.d("Test", "position = " + position);
                startActivity(new Intent(LinearVerticalActivity.this, SecondActivity.class));
            }
        });
        final Random random = new Random();
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<IdModel> list = new ArrayList(mAdapter.getItems());
                int index = random.nextInt(list.size());
                list.add(index, new IdModel(list.size(), "add " + index));
                index = random.nextInt(list.size());
                IdModel model = list.get(index);
                IdModel newModel = new IdModel(model.getId(), "payloads!!!!!!!" + index);
                list.set(index, newModel);

                model = list.get(0);
                newModel = new IdModel(model.getId(), "payloads!!!!!!!" + index);
                list.set(0, newModel);

                mAdapter.setItems(list, new DiffCapable<String>() {
                    @Override
                    public boolean isSame(int diff, Object oldItem, Object newItem) {
                        if (oldItem instanceof IdModel && newItem instanceof IdModel) {
                            final IdModel oldModel = (IdModel) oldItem;
                            final IdModel newModel = (IdModel) newItem;
                            if (diff == DIFF_ITEM) {
                                return oldModel.getId() == newModel.getId();
                            } else if (diff == DIFF_CONTENT) {
                                return oldModel.getContent().equals(newModel.getContent());
                            }
                        }
                        return oldItem.equals(newItem);
                    }

                    @Override
                    public String payloads(Object oldItem, Object newItem) {
                        if (oldItem instanceof IdModel && newItem instanceof IdModel) {
                            final IdModel oldModel = (IdModel) oldItem;
                            final IdModel newModel = (IdModel) newItem;
                            if (!oldModel.getContent().equals(newModel.getContent())) {
                                return newModel.getContent();
                            }
                        }
                        return null;
                    }
                });
            }
        });
    }

    private static class ItemViewDelegate1 extends PayloadsItemViewDelegate<IdModel, String, ViewHolder> {
        public ItemViewDelegate1() {
            super(android.R.layout.simple_list_item_1);
        }

        @Override
        public void convert(@NonNull ViewHolder holder, IdModel idModel, int position) {
            TextView textView = holder.findView(android.R.id.text1);
            textView.setText("item1 position = " + position + ", s=" + idModel.getContent());
        }

        @Override
        public void payloads(ViewHolder holder, int position, List<String> payloads) {
            super.payloads(holder, position, payloads);
            Log.e("RecyclerAdapter", "payloads=" + payloads);
            TextView textView = holder.findView(android.R.id.text1);
            textView.setText(payloads.get(0));
        }
    }

    private static class ItemViewDelegate2 extends PayloadsItemViewDelegate<IdModel, String, ViewHolder> {

        public ItemViewDelegate2() {
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
            super.payloads(holder, position, payloads);
            TextView textView = holder.findView(android.R.id.text2);
            textView.setText(payloads.get(0));
        }
    }
}
