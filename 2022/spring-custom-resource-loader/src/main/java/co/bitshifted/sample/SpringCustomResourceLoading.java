package co.bitshifted.sample;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Hello world!
 *
 */
public class SpringCustomResourceLoading
{
    public static void main( String[] args ) throws Exception {
        var ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        var svc = ctx.getBean(ZipService.class);
        svc.loadResource("zip://./archive.zip!file1.txt");
        svc.getCustomResource();
    }
}
