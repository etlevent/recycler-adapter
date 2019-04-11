package cherry.android.recycler.sample.linear;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cherry.android.recycler.sample.RecyclerActivity;
import cherry.android.recycler.sample.delegate.Foo1Delegate;
import cherry.android.recycler.sample.delegate.Foo3Delegate;
import cherry.android.recycler.sample.model.Foo1;
import cherry.android.recycler.sample.model.Foo3;
import ext.android.adapter.RecyclerAdapter;

public class ThirdActivity extends RecyclerActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Random random = new Random();
        List<Foo1> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Foo1 foo;
            if (random.nextInt(2) == 0) {
                foo = new Foo1();
            } else {
                foo = new Foo3();
            }
            list.add(foo);
        }
        RecyclerAdapter adapter = new RecyclerAdapter(list);
        adapter.addDelegate(Foo3.class, new Foo3Delegate());
        adapter.addDelegate(Foo1.class, new Foo1Delegate());
        mRecyclerView.setAdapter(adapter);
    }
}
