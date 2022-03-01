package co.bitshifted.sample.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertyBean {
    @Value("${some.property}")
    private String stringProperty;

    @Value("${some.integer}")
    private int someInteger;

    public void show() {
        System.out.println(String.format("String property: %s, integer property: %d", stringProperty, someInteger));
    }
}
