package cherry.android.recycler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/6/2.
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    public static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL = LinearLayoutManager.VERTICAL;

    private int mOrientation = VERTICAL;
    private int mGap = 1;
    private Paint mPaint;
    private boolean mUseSpace;

    public DividerItemDecoration(Context context, int orientation) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0xff9e9e9e);
        mOrientation = orientation;
    }

    public void setGap(int gap) {
        mGap = gap;
    }

    public void useSpace(boolean flag) {
        mUseSpace = flag;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if (position != 0) {
            if (mOrientation == VERTICAL)
                outRect.set(0, mGap, 0, 0);
            else
                outRect.set(mGap, 0, 0, 0);
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mUseSpace) return;
        for (int i = 0; i < parent.getChildCount(); i++) {
            final View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);
            if (position != 0) {
                int top = child.getTop() - mGap;
                int bottom = child.getTop();
                int left = parent.getPaddingLeft();
                int right = parent.getWidth() - parent.getPaddingRight();
                c.drawRect(left, top, right, bottom, mPaint);

            }
        }
    }
}
