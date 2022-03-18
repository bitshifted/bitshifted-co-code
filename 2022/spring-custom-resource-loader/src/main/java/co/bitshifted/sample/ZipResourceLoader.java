package co.bitshifted.sample;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class ZipResourceLoader implements ResourceLoader {

    public static final String ZIP_PREFIX = "zip://";

    private final ResourceLoader delegate;

    public ZipResourceLoader(ResourceLoader delegate) {
        this.delegate = delegate;
    }

    @Override
    public Resource getResource(String location) {
        if(location.startsWith(ZIP_PREFIX)) {
            var path = location.substring(ZIP_PREFIX.length());
            int entryIndex = path.lastIndexOf('!');
            String zipFilePath = path.substring(0, entryIndex);
            String entryPath = path.substring(entryIndex + 1);
            var zipEntry = new ZipEntryResource();
            return zipEntry.getResource(zipFilePath, entryPath);
        }
        return delegate.getResource(location);
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.delegate.getClassLoader();
    }
}
