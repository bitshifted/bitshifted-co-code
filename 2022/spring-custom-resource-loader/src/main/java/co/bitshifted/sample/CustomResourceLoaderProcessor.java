package co.bitshifted.sample;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class CustomResourceLoaderProcessor implements  ResourceLoaderAware, ProtocolResolver {

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        if(DefaultResourceLoader.class.isAssignableFrom(resourceLoader.getClass())) {
            ((DefaultResourceLoader)resourceLoader).addProtocolResolver(this);
        } else {
            System.out.println("Could not assign protocol loader.");
        }
    }


    @Override
    public Resource resolve(String location, ResourceLoader resourceLoader) {
        if(location.startsWith(ZipResourceLoader.ZIP_PREFIX)) {
            var loader = new ZipResourceLoader(resourceLoader);
            return loader.getResource(location);
        }
        return resourceLoader.getResource(location);
    }
}
