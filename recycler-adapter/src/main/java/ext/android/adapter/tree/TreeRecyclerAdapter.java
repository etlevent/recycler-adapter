package ext.android.adapter.tree;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ext.android.adapter.RecyclerAdapter;
import ext.android.adapter.diff.DiffCapable;

public class TreeRecyclerAdapter extends RecyclerAdapter {
    private List<?> mSource;
    @Nullable
    private OnExpandedChangedListener mOnExpandedChangedListener;
    private OnItemClickListener mActualOnItemClickListener;
    private State mState;

    private final OnItemClickListener mInternalOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View itemView, RecyclerView.ViewHolder holder, int position) {
            final List<?> items = TreeRecyclerAdapter.super.getItems();
            if (items != null) {
                final Object item = items.get(position);
                if (item instanceof NodeGroup) {
                    final NodeGroup nodeGroup = (NodeGroup) item;
                    if (nodeGroup.isDispatchExpandAutomatic()) {
                        dispatchExpandedChanged(nodeGroup);
                    }
                }
            }
            if (mActualOnItemClickListener != null) {
                mActualOnItemClickListener.onItemClick(itemView, holder, position);
            }
        }
    };

    public TreeRecyclerAdapter() {
        this(State.EXPAND);
    }

    public TreeRecyclerAdapter(State state) {
        this.mState = state;
        super.setOnItemClickListener(mInternalOnItemClickListener);
    }

    @Override
    public void setItems(@Nullable List<?> items) {
        this.mSource = items;
        super.setItems(flattenSource(items));
    }

    @Override
    public <P> void setItems(@Nullable List<?> items, DiffCapable<P> diffCapable) {
        this.mSource = items;
        super.setItems(flattenSource(items), diffCapable);
    }

    @Nullable
    @Override
    public List<?> getItems() {
        return this.mSource;
    }


    @Nullable
    public List<?> getRealItems() {
        return super.getItems();
    }

    public void setOnExpandedChangedListener(@Nullable OnExpandedChangedListener listener) {
        this.mOnExpandedChangedListener = listener;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mActualOnItemClickListener = listener;
    }

    interface OnExpandedChangedListener {
        void onExpanded(NodeGroup group, int position);

        void onCollapsed(NodeGroup group, int position);
    }

    public void dispatchExpandedChanged(NodeGroup nodeGroup) {
        final List<?> actualItems = super.getItems();
        if (actualItems == null) {
            return;
        }
        final int index = actualItems.indexOf(nodeGroup);
        if (nodeGroup.isExpanded()) {
            nodeGroup.setExpanded(false);
            onNodeGroupCollapsed(nodeGroup);
            if (mOnExpandedChangedListener != null) {
                mOnExpandedChangedListener.onCollapsed(nodeGroup, index);
            }
        } else {
            nodeGroup.setExpanded(true);
            onNodeGroupExpanded(nodeGroup);
            if (mOnExpandedChangedListener != null) {
                mOnExpandedChangedListener.onExpanded(nodeGroup, index);
            }
        }
    }

    private void onNodeGroupExpanded(NodeGroup nodeGroup) {
        final List actualItems = super.getItems();
        if (actualItems == null) {
            return;
        }
        final List<?> recursiveChild = unfoldIfNodeGroup(nodeGroup, State.ALL);
        if (recursiveChild == null || recursiveChild.isEmpty()) {
            return;
        }
        final int index = actualItems.indexOf(nodeGroup);
        actualItems.addAll(index + 1, recursiveChild);
        notifyItemChanged(index);
        notifyItemRangeInserted(index + 1, recursiveChild.size());
    }

    private void onNodeGroupCollapsed(NodeGroup nodeGroup) {
        final List actualItems = super.getItems();
        if (actualItems == null) {
            return;
        }
        final List<?> recursiveChild = unfoldIfNodeGroup(nodeGroup, State.ALL);
        if (recursiveChild == null || recursiveChild.isEmpty()) {
            return;
        }
        final int index = actualItems.indexOf(nodeGroup);
        actualItems.removeAll(recursiveChild);
        notifyItemChanged(index);
        notifyItemRangeRemoved(index + 1, recursiveChild.size());
    }

    @Nullable
    private List<?> flattenSource(@Nullable List<?> source) {
        if (source == null) {
            return null;
        }
        List<? super Object> dst = new ArrayList<>(source.size());
        for (Object o : source) {
            if (o == null) continue;
            dst.add(o);
            if (o instanceof NodeGroup) {
                final List<?> result = unfoldIfNodeGroup((NodeGroup) o, this.mState);
                if (result == null) continue;
                dst.addAll(result);
            }
        }
        return dst;
    }

    @Nullable
    private static List<?> unfoldIfNodeGroup(NodeGroup nodeGroup, State state) {
        if (nodeGroup.getChildren() == null || nodeGroup.getChildren().isEmpty()) {
            return null;
        }
        if (state == State.EXPAND && !nodeGroup.isExpanded()) {
            return null;
        }
        List<? super Object> dst = new ArrayList<>(nodeGroup.getChildren().size());
        for (Object child : nodeGroup.getChildren()) {
            if (child == null) continue;
            dst.add(child);
            if (child instanceof NodeGroup) {
                final List<?> result = unfoldIfNodeGroup((NodeGroup) child, state);
                if (result == null) continue;
                dst.addAll(result);
            }
        }
        return dst;
    }

    enum State {
        ALL,
        EXPAND
    }
}
