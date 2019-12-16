package cherry.android.recycler.sample.tree;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cherry.android.recycler.sample.RecyclerActivity;
import cherry.android.recycler.sample.delegate.ChildDelegate;
import cherry.android.recycler.sample.delegate.ParentDelegate;
import cherry.android.recycler.sample.model.Child;
import cherry.android.recycler.sample.model.Parent;
import ext.android.adapter.tree.TreeRecyclerAdapter;

public class LinearTreeNodeActivity extends RecyclerActivity {
    private TreeRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TreeRecyclerAdapter();
        mAdapter.addDelegate(Parent.class, new ParentDelegate());
        mAdapter.addDelegate(Child.class, new ChildDelegate());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.setItems(randomData());
            }
        }, 100);
    }

    private static List<Parent> randomData() {
        Random random = new Random();
        int count = random.nextInt(50) + 1;
        List<Parent> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Parent parent = new Parent("parent " + i);
            int childCount = random.nextInt(3) + 1;
            if (childCount != 0) {
                List<Child> children = new ArrayList<>();
                for (int j = 0; j < childCount; j++) {
                    children.add(new Child("Child" + j, j));
                }
                parent.setChildren(children);
            }
            result.add(parent);
        }
        return result;
    }
}
