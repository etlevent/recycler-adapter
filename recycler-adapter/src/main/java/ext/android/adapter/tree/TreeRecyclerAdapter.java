package ext.android.adapter.tree;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ext.android.adapter.RecyclerAdapter;

public class TreeRecyclerAdapter extends RecyclerAdapter {
    private List<?> mSource;
    @Nullable
    private OnExpandedChangedListener mOnExpandedChangedListener;
    private OnItemClickListener mActualOnItemClickListener;
    private final State mState;

    private final OnItemClickListener mInternalOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View itemView, RecyclerView.ViewHolder holder, int position) {
            final List<?> items = TreeRecyclerAdapter.super.getList();
            final Object item = items.get(position);
            if (item instanceof NodeGroup) {
                final NodeGroup nodeGroup = (NodeGroup) item;
                if (nodeGroup.isDispatchExpandAutomatic()) {
                    dispatchExpandedChanged(nodeGroup);
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
    public void setList(@Nullable List<?> list) {
        this.mSource = list;
        super.setList(flattenSource(list));
    }

    @Override
    public void submitList(@Nullable List<?> list) {
        this.mSource = list;
        super.submitList(flattenSource(list));
    }

    @NonNull
    @Override
    public List<?> getList() {
        if (this.mSource == null)
            return Collections.emptyList();
        return this.mSource;
    }


    @Nullable
    public List<?> getRealList() {
        return super.getList();
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
        final List<?> actualItems = new ArrayList<>(super.getList());
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
        final List<Object> actualList = new ArrayList<>(super.getList());
        final List<?> recursiveChild = unfoldIfNodeGroup(nodeGroup, State.ALL);
        if (recursiveChild == null || recursiveChild.isEmpty()) {
            return;
        }
        final int index = actualList.indexOf(nodeGroup);
        actualList.addAll(index + 1, recursiveChild);
        super.setList(actualList);
        notifyItemChanged(index);
        notifyItemRangeInserted(index + 1, recursiveChild.size());
    }

    private void onNodeGroupCollapsed(NodeGroup nodeGroup) {
        final List<Object> actualList = new ArrayList<>(super.getList());
        final List<?> recursiveChild = unfoldIfNodeGroup(nodeGroup, State.ALL);
        if (recursiveChild == null || recursiveChild.isEmpty()) {
            return;
        }
        final int index = actualList.indexOf(nodeGroup);
        actualList.removeAll(recursiveChild);
        super.setList(actualList);
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
