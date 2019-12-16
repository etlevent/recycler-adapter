package ext.android.adapter.tree;

import android.support.annotation.Nullable;

import java.util.List;

public class NodeGroup implements Node {
    @Nullable
    private List<?> children;
    private boolean isExpanded;
    private boolean dispatchExpandAutomatic = true;

    @Nullable
    public List<?> getChildren() {
        return children;
    }

    public void setChildren(@Nullable List<?> children) {
        this.children = children;
    }

    void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public boolean isDispatchExpandAutomatic() {
        return dispatchExpandAutomatic;
    }

    public void setDispatchExpandAutomatic(boolean dispathExpandAutomatic) {
        this.dispatchExpandAutomatic = dispathExpandAutomatic;
    }
}
