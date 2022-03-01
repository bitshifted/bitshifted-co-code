package co.bitshifted.sample.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class AnotherFooServiceImpl implements FooService {

    @Override
    public void someMethod(String arg) {
        System.out.println("running AnotherFooServiceImple.someMethod(" + arg + ")");
    }
}
