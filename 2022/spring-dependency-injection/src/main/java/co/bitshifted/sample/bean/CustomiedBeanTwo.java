package co.bitshifted.sample.bean;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class CustomiedBeanTwo {

    @PostConstruct
    public void onStart() {
        System.out.println("Calling method annotated with @PostConstruct");
    }

    @PreDestroy
    public void onStop() {
        System.out.println("Calling method annotated with @PreDestroy");
    }
}
