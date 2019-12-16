package cherry.android.recycler.sample.model;

import ext.android.adapter.tree.NodeGroup;

public class Parent extends NodeGroup {
    private String name;

    public Parent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
