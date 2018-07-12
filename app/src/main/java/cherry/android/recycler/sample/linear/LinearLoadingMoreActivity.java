package cherry.android.recycler.sample.linear;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cherry.android.recycler.ViewHolder;
import cherry.android.recycler.sample.R;
import cherry.android.recycler.sample.RecyclerActivity;
import cherry.android.recycler.sample.adapter.StringSimpleAdapter;
import cherry.android.recycler.wrapper.LoadMoreWrapper;

public class LinearLoadingMoreActivity extends RecyclerActivity {
    private static final String TAG = "Recycler";
    private StringSimpleAdapter mAdapter;
    private List<String> mItems;
    //    private LoadMoreWrapper mWrapper;
    private LoadMoreWrapper mWrapper;
    private int count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItems = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new StringSimpleAdapter();
//        mWrapper = new LoadMoreWrapper(mAdapter, R.layout.layout_loading_more);
        mWrapper = new LoadMoreWrapper(mAdapter, R.layout.layout_loading_more);
        mWrapper.setOnStateChangedListener(new LoadMoreWrapper.OnStateChangedListener() {
            @Override
            public void onStateChanged(int state, ViewHolder viewHolder) {
                View loadingView = viewHolder.findView(R.id.loading);
                View noMoreView = viewHolder.findView(R.id.no_more_view);
                switch (state) {
                    case LoadMoreWrapper.State.HIDE:
                        viewHolder.itemView.setVisibility(View.GONE);
                        break;
                    case LoadMoreWrapper.State.LOADING_MORE:
                        viewHolder.itemView.setVisibility(View.VISIBLE);
                        loadingView.setVisibility(View.VISIBLE);
                        noMoreView.setVisibility(View.GONE);
                        break;
                    case LoadMoreWrapper.State.LOADING_FAIL:
                        viewHolder.itemView.setVisibility(View.VISIBLE);
                        break;
                    case LoadMoreWrapper.State.NO_MORE:
                        viewHolder.itemView.setVisibility(View.VISIBLE);
                        loadingView.setVisibility(View.GONE);
                        noMoreView.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });
        mWrapper.setOnLoadMoreListener(new LoadMoreWrapper.SimpleLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.i(TAG, "onLoadMore");
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (count > 5) {
                            mWrapper.setState(LoadMoreWrapper.State.NO_MORE);
                        }
                        mItems.add("load More item. " + count);
                        mAdapter.notifyItemChanged(mItems.size() - 1);
                        count++;
                        if (count == 3) {
                            Log.v(TAG, "notify changed.");
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }, 1500);
            }
        });
        mRecyclerView.setAdapter(mWrapper);
        for (int i = 0; i < 15; i++) {
            mItems.add("it is item " + i);
        }
        mAdapter.setItems(mItems);
        mWrapper.setState(LoadMoreWrapper.State.HIDE);
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.w(TAG, "notify");
                mWrapper.setState(LoadMoreWrapper.State.LOADING_MORE);
            }
        }, 5000);
    }
}
