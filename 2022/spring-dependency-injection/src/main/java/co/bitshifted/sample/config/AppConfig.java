package co.bitshifted.sample.config;

import co.bitshifted.sample.bean.BarBean;
import co.bitshifted.sample.bean.FooBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = {"co.bitshifted.sample"})
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    @Scope("prototype")
    public BarBean barBean() {
        return new BarBean();
    }

    @Bean("fooBeanName")
    public FooBean fooBean() {
        return new FooBean(barBean());
    }
}
