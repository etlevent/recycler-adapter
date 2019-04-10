package ext.android.adapter.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "DividerItemDecoration";
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private Drawable mDivider;
    private final Rect mBounds = new Rect();

    public DividerItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        if (mDivider == null) {
            Log.w(TAG, "@android:attr/listDivider was not set in the theme used for this "
                    + "DividerItemDecoration. Please set that attribute all call setDrawable()");
        }
        a.recycle();
    }


    public void setDrawable(@NonNull Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable cannot be null.");
        }
        mDivider = drawable;
    }


    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getLayoutManager() == null || mDivider == null) {
            return;
        }
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int left;
        final int right;
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
            //final int top = bottom - mDivider.getIntrinsicHeight();
            final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            final int decoratedHeight = layoutManager.getBottomDecorationHeight(child) - layoutManager.getTopDecorationHeight(child);
            final int top = bottom - decoratedHeight;
            mDivider.setBounds(mBounds.left, top, mBounds.right, bottom);
            mDivider.draw(canvas);
        }
        canvas.restore();
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int top;
        final int bottom;
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top,
                    parent.getWidth() - parent.getPaddingRight(), bottom);
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            final int right = mBounds.right + Math.round(child.getTranslationX());
            //final int left = right - mDivider.getIntrinsicWidth();
            final int decoratedWidth;
            final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            int left = 0;
            if (layoutManager != null) {
                decoratedWidth = layoutManager.getRightDecorationWidth(child) - layoutManager.getLeftDecorationWidth(child);
                left = right - decoratedWidth;
            }
            mDivider.setBounds(left, mBounds.top, right, mBounds.bottom);
            mDivider.draw(canvas);
        }
        canvas.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (mDivider == null) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        final int itemPosition = parent.getChildAdapterPosition(view);
        final int childCount = parent.getAdapter().getItemCount();
        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        boolean isLastRow = isLastRow(layoutManager, itemPosition, childCount);
        boolean isLastColumn = isLastColumn(layoutManager, itemPosition, childCount);
        Log.i(TAG, "itemPosition=[" + itemPosition + "] childCount=[" + childCount + "]");
        Log.d(TAG, "isLastRow=[" + isLastRow + "] isLastColumn=[" + isLastColumn + "]");
        if (isLastRow && isLastColumn) {
            outRect.set(0, 0, 0, 0);
        } else if (isLastRow) {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        } else if (isLastColumn) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());
        }
    }

    private boolean isLastRow(RecyclerView.LayoutManager layoutManager, int itemPosition, int childCount) {
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final int spanCount = gridLayoutManager.getSpanCount();
            int lastRowFirstPosition = spanCount * ((childCount - 1) / spanCount);
            return itemPosition >= lastRowFirstPosition;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            final StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            final int spanCount = staggeredGridLayoutManager.getSpanCount();
            final int orientation = staggeredGridLayoutManager.getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                int lastRowFirstPosition = spanCount * ((childCount - 1) / spanCount);
                return itemPosition >= lastRowFirstPosition;
            } else {
                return (itemPosition + 1) % spanCount == 0;
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            final int orientation = linearLayoutManager.getOrientation();
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                return true;
            } else {
                return itemPosition == childCount - 1;
            }
        }
        return false;
    }

    private boolean isLastColumn(RecyclerView.LayoutManager layoutManager, int itemPosition, int childCount) {
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final int spanCount = gridLayoutManager.getSpanCount();
            return (itemPosition + 1) % spanCount == 0;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            final StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            final int spanCount = staggeredGridLayoutManager.getSpanCount();
            final int orientation = staggeredGridLayoutManager.getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                return (itemPosition + 1) % spanCount == 0;
            } else {
                int lastRowFirstPosition = spanCount * ((childCount - 1) / spanCount);
                return itemPosition >= lastRowFirstPosition;
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            final int orientation = linearLayoutManager.getOrientation();
            if (orientation == LinearLayoutManager.VERTICAL) {
                return true;
            } else {
                return itemPosition == childCount - 1;
            }
        }
        return false;
    }
}
