package co.bitshifted.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;


import java.io.IOException;

@Service
public class ZipService {
    @Autowired
    private ResourceLoader resourceLoader;
    @Value("zip://./archive.zip!file2.txt")
    private Resource customResource;

    public void loadResource(String resourceUrl) throws IOException {
        var resource = resourceLoader.getResource(resourceUrl);
        var txt = new String(resource.getInputStream().readAllBytes());
        System.out.println("File content: " + txt);
    }

    public void getCustomResource() throws IOException {
        var txt = new String(customResource.getInputStream().readAllBytes());
        System.out.println("Resource from property: " + txt);
    }
}
