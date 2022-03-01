package co.bitshifted.sample.bean;

public class FooBean {

    private final BarBean barBean;

    public FooBean(BarBean barBean) {
        this.barBean = barBean;
    }

    public void fooMethod() {
        System.out.println("fooMethod() called");
    }
}
