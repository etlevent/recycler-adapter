package cherry.android.recycler.sample.linear;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ext.android.adapter.delegate.ItemViewDelegate;
import ext.android.adapter.RecyclerAdapter;
import ext.android.adapter.ItemViewDelegateConverter;
import cherry.android.recycler.sample.R;
import cherry.android.recycler.sample.delegate.SimpleDelegate1;
import cherry.android.recycler.sample.delegate.SimpleDelegate2;
import cherry.android.recycler.sample.model.Foo;
import cherry.android.recycler.sample.model.Foo1;
import cherry.android.recycler.sample.model.Foo2;
import cherry.android.recycler.sample.model.IFoo;

/**
 * Created by ROOT on 2017/8/9.
 */

public class SecondActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recycler_view);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerAdapter();
        mAdapter.addDelegate(Foo.class)
                .bindDelegate(new SimpleDelegate1(), new SimpleDelegate2())
                .to(new ItemViewDelegateConverter<Foo>() {
                    @Override
                    public Class<? extends ItemViewDelegate> convert(Foo foo, int position) {
                        IFoo iFoo = foo.getBody();
                        if (iFoo instanceof Foo1)
                            return SimpleDelegate1.class;
                        else
                            return SimpleDelegate2.class;
                    }
                });
        Random random = new Random();
        List<Foo> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Foo foo;
            if (random.nextInt(2) == 0) {
                foo = new Foo<>(new Foo1());
            } else {
                foo = new Foo(new Foo2());
            }
            list.add(foo);
        }
        mAdapter.setItems(list);
        recyclerView.setAdapter(mAdapter);
    }
}
