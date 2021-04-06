package cherry.android.recycler.sample;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Random;

import ext.android.adapter.decoration.DividerItemDecoration;

public class RecyclerActivity extends AppCompatActivity {

    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mRecyclerView;
    private DividerItemDecoration mDefaultItemDecoration;
    private Random mRandom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recycler_view);
        mRandom = new Random();
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setEnabled(swipeToRefreshEnabled());
        mRecyclerView = findViewById(R.id.recycler);
        mDefaultItemDecoration = new DividerItemDecoration(this);
        mDefaultItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.shape_divider));
        mRecyclerView.addItemDecoration(mDefaultItemDecoration);
    }

    protected RecyclerView.ItemDecoration getDefaultItemDecoration() {
        return mDefaultItemDecoration;
    }

    protected boolean swipeToRefreshEnabled() {
        return false;
    }

    protected int nextInt(int bound) {
        return mRandom.nextInt(bound);
    }

    protected int nextInt() {
        return mRandom.nextInt();
    }
}
