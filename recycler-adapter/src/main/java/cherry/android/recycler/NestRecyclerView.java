package cherry.android.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by Administrator on 2017/6/9.
 */

public class NestRecyclerView extends RecyclerView {

    private static final String TAG = "NestRecyclerView";

    private static final int INVALID_POINTER = -1;

    private int mScrollPointerId = INVALID_POINTER;
    private int mTouchSlop;
    private int mInitialTouchX;
    private int mInitialTouchY;

    public NestRecyclerView(Context context) {
        this(context, null);
    }

    public NestRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    @Override
    public void setScrollingTouchSlop(int slopConstant) {
        super.setScrollingTouchSlop(slopConstant);
        final ViewConfiguration vc = ViewConfiguration.get(getContext());
        switch (slopConstant) {
            default:
                Log.w(TAG, "setScrollingTouchSlop(): bad argument constant "
                        + slopConstant + "; using default value");
                // fall-through
            case TOUCH_SLOP_DEFAULT:
                mTouchSlop = vc.getScaledTouchSlop();
                break;

            case TOUCH_SLOP_PAGING:
                mTouchSlop = vc.getScaledPagingTouchSlop();
                break;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        final int action = MotionEventCompat.getActionMasked(e);
        final int actionIndex = MotionEventCompat.getActionIndex(e);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScrollPointerId = e.getPointerId(0);
                mInitialTouchX = (int) (e.getX() + 0.5f);
                mInitialTouchY = (int) (e.getY() + 0.5f);
                break;

            case MotionEventCompat.ACTION_POINTER_DOWN:
                mScrollPointerId = e.getPointerId(actionIndex);
                mInitialTouchX = (int) (e.getX(actionIndex) + 0.5f);
                mInitialTouchY = (int) (e.getY(actionIndex) + 0.5f);
                break;

            case MotionEvent.ACTION_MOVE: {
                final int index = e.findPointerIndex(mScrollPointerId);
                if (index < 0) {
                    Log.e(TAG, "Error processing scroll; pointer index for id " +
                            mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    return false;
                }

                final int x = (int) (e.getX(index) + 0.5f);
                final int y = (int) (e.getY(index) + 0.5f);
                if (getScrollState() != SCROLL_STATE_DRAGGING) {
                    final int dx = x - mInitialTouchX;
                    final int dy = y - mInitialTouchY;
                    final boolean canScrollHorizontally = getLayoutManager().canScrollHorizontally();
                    final boolean canScrollVertically = getLayoutManager().canScrollVertically();
                    boolean startScroll = false;
                    if (canScrollHorizontally && Math.abs(dx) > mTouchSlop
                            && Math.abs(dx) >= Math.abs(dy)) {
                        startScroll = true;
                    }
                    if (canScrollVertically && Math.abs(dy) > mTouchSlop
                            && Math.abs(dy) >= Math.abs(dx)) {
                        startScroll = true;
                    }
                    return startScroll && super.onInterceptTouchEvent(e);
                }
            }
            break;
        }
        return super.onInterceptTouchEvent(e);
    }
}
