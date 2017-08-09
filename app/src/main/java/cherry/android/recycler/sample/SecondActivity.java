package cherry.android.recycler.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cherry.android.recycler.ItemViewDelegate;
import cherry.android.recycler.RecyclerAdapter;
import cherry.android.recycler.ViewChooser;
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
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerAdapter();
        mAdapter.addDelegate(Foo.class)
                .bindDelegate(new SimpleDelegate1(), new SimpleDelegate2())
                .to(new ViewChooser<Foo>() {
                    @Override
                    public Class<? extends ItemViewDelegate<? extends Foo, ? extends RecyclerView.ViewHolder>> choose(Foo foo, int position) {
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
