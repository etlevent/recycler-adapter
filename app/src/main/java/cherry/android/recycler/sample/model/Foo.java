package cherry.android.recycler.sample.model;

/**
 * Created by ROOT on 2017/8/9.
 */

public class Foo<B extends IFoo> {

    private B body;

    public Foo(B body) {
        this.body = body;
    }

    public B getBody() {
        return body;
    }
}
