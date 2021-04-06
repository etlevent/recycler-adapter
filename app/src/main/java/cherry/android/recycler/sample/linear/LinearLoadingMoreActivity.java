package cherry.android.recycler.sample.linear;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cherry.android.recycler.sample.RecyclerActivity;
import cherry.android.recycler.sample.adapter.StringSimpleAdapter;
import cherry.android.recycler.sample.loadmore.SimpleLoadMoreView;
import ext.android.adapter.wrapper.LoadMoreWrapper;

public class LinearLoadingMoreActivity extends RecyclerActivity {
    private static final String TAG = "Recycler";
    private StringSimpleAdapter mAdapter;
    private List<String> mItems;
    private LoadMoreWrapper mWrapper;
    private int count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItems = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new StringSimpleAdapter();
        mWrapper = new LoadMoreWrapper(mAdapter, new SimpleLoadMoreView());
        mWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.i(TAG, "onLoadMore");
                if (count > 5) {
                    mWrapper.setState(LoadMoreWrapper.STATE_NO_MORE);
                } else {
                    final String s = "load More item. " + count;
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mItems.add(s);
                            mAdapter.notifyItemChanged(mItems.size() - 1);
                        }
                    }, 1500);
                }
                count++;
            }
        });
        mRecyclerView.setAdapter(mWrapper);
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 15; i++) {
                    mItems.add("it is item " + i);
                }
                mAdapter.setList(mItems);
                mAdapter.notifyDataSetChanged();
            }
        }, 300);
    }
}
