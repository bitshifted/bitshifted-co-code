package co.bitshifted.sample;

import co.bitshifted.sample.bean.BarBean;
import co.bitshifted.sample.bean.FooBean;
import co.bitshifted.sample.bean.PropertyBean;
import co.bitshifted.sample.component.FooComponent;
import co.bitshifted.sample.config.AppConfig;
import co.bitshifted.sample.service.FooService;
import co.bitshifted.sample.service.RepoService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        var ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        var barBean = ctx.getBean("barBean", BarBean.class);
        barBean.barMethod();
        var fooBean = ctx.getBean("fooBeanName", FooBean.class);
        fooBean.fooMethod();
        // test bean scopes
        for(int i = 0;i < 5;i++) {
            System.out.println("Foo bean hashcode: " + ctx.getBean(FooBean.class).hashCode());
            System.out.println("Bar bean hash code: " + ctx.getBean(BarBean.class).hashCode());
        }
        var component = ctx.getBean(FooComponent.class);
        component.componentMethod();
        System.out.println("====== @Qualifier example ========");
        var svc = ctx.getBean(RepoService.class);
        svc.methodOne();
        svc.methodTwo();
        System.out.println("===== @Value example ====");
        var prop = ctx.getBean(PropertyBean.class);
        prop.show();
    }
}
