package cherry.android.recycler.layout;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Administrator on 2017/6/15.
 */

public class OverlayCardLayoutManager extends RecyclerView.LayoutManager {
    private static final String TAG = "OverlayCard";

    private static final int MAX_SHOW_COUNT = 4;
    private static final float SCALE_GAP = 0.1f;
    private static final float Y_GAP = 40;

    private RecyclerView mRecyclerView;
    private ItemTouchHelper mItemTouchHelper;

    private List mItems;

    public OverlayCardLayoutManager(List items) {
        super();
        this.mItems = items;
        mItemTouchHelper = new ItemTouchHelper(new OverlayItemTouchCallback());
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        super.onMeasure(recycler, state, widthSpec, heightSpec);
        if (mRecyclerView == null) {
            mRecyclerView = getRecyclerView();
            mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        }
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        //super.onLayoutChildren(recycler, state);
        Log.e(TAG, "recycler=[" + recycler + "], state=[" + state + "]");
        detachAndScrapAttachedViews(recycler);
        final int itemCount = getItemCount();
        int bottomPosition;
        if (itemCount < MAX_SHOW_COUNT) {
            bottomPosition = 0;
        } else {
            bottomPosition = itemCount - MAX_SHOW_COUNT;
        }
        for (int position = bottomPosition; position < itemCount; position++) {
            final View child = recycler.getViewForPosition(position);
            addView(child);
            measureChildWithMargins(child, 0, 0);
            int widthSpace = getWidth() - getDecoratedMeasuredWidth(child);
            int heightSpace = getHeight() - getDecoratedMeasuredHeight(child);
            final int leftMargin = widthSpace / 2;
            final int topMargin = heightSpace / 2;
            final int rightMargin = leftMargin + getDecoratedMeasuredWidth(child);
            final int bottomMargin = topMargin + getDecoratedMeasuredHeight(child);
            //居中显示
            layoutDecoratedWithMargins(child, leftMargin, topMargin, rightMargin, bottomMargin);
            int topLevel = itemCount - position - 1;
            if (topLevel == 0)
                continue;
            if (topLevel < MAX_SHOW_COUNT - 1) {
                child.setTranslationY(Y_GAP * topLevel);
                child.setScaleX(1 - SCALE_GAP * topLevel);
                child.setScaleY(1 - SCALE_GAP * topLevel);
            } else {
                child.setTranslationY(Y_GAP * (topLevel - 1));
                child.setScaleX(1 - SCALE_GAP * (topLevel - 1));
                child.setScaleY(1 - SCALE_GAP * (topLevel - 1));

            }
        }
    }

    private RecyclerView getRecyclerView() {
        RecyclerView recyclerView = null;
        try {
            Field field = getClass().getSuperclass().getDeclaredField("mRecyclerView");
            field.setAccessible(true);
            recyclerView = (RecyclerView) field.get(this);
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "[NoSuchFieldException]", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "[IllegalAccessException]", e);
        }
        return recyclerView;
    }

    private class OverlayItemTouchCallback extends ItemTouchHelper.SimpleCallback {

        public OverlayItemTouchCallback() {
            super(0, ItemTouchHelper.UP
                    | ItemTouchHelper.DOWN
                    | ItemTouchHelper.LEFT
                    | ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            Log.i(TAG, "onSwipe");
            int position = viewHolder.getLayoutPosition();
//            mItems.add(0, mItems.remove(position));
//            mRecyclerView.getAdapter().notifyDataSetChanged();
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            double swipedVal = Math.sqrt(dX * dX + dY * dY);
            double fraction = swipedVal / (recyclerView.getWidth() * 0.5f);
            if (fraction > 1)
                fraction = 1;
            Log.i(TAG, "onChildDraw");
            final int count = recyclerView.getChildCount();
            for (int i = 0; i < count; i++) {
                final View child = recyclerView.getChildAt(i);
                int topLevel = count - 1 - i;
                if (topLevel > 0) {
                    child.setScaleX((float) (1 - SCALE_GAP * topLevel + fraction * SCALE_GAP));
                    if (topLevel < MAX_SHOW_COUNT - 1) {
                        child.setScaleY((float) (1 - SCALE_GAP * topLevel + fraction * SCALE_GAP));
                        child.setTranslationY((float) (Y_GAP * topLevel - fraction * Y_GAP));
                    }
                }
            }
        }
    }
}
