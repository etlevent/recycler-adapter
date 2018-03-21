package cherry.android.recycler.sample.model;

/**
 * Created by roothost on 2018/3/21.
 */

public class IdModel {
    private int id;
    private String content;

    public IdModel(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
