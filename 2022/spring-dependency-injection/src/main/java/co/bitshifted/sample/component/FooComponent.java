package co.bitshifted.sample.component;

import co.bitshifted.sample.service.FooService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FooComponent {
    @Autowired
    private FooService fooService;

    public void componentMethod() {
        fooService.someMethod("argument");
    }
}
