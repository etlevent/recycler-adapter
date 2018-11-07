package cherry.android.recycler.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.Random;

import cherry.android.recycler.DividerItemDecoration;

public class RecyclerActivity extends AppCompatActivity {

    protected RecyclerView mRecyclerView;
    private DividerItemDecoration mDefaultItemDecoration;
    private Random mRandom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recycler_view);
        mRandom = new Random();
        mRecyclerView = findViewById(R.id.recycler);
        mDefaultItemDecoration = new DividerItemDecoration(this);
        mDefaultItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.shape_divider));
    }

    protected RecyclerView.ItemDecoration getDefaultItemDecoration() {
        return mDefaultItemDecoration;
    }

    protected int nextInt(int bound) {
        return mRandom.nextInt(bound);
    }

    protected int nextInt() {
        return mRandom.nextInt();
    }
}