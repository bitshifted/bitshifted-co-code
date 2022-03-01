package co.bitshifted.sample.service;

import org.springframework.stereotype.Service;

@Service
public class FooServiceImpl implements FooService {

    @Override
    public void someMethod(String arg) {
        System.out.println("Running FooService.someMethod(" + arg + ")");
    }
}
