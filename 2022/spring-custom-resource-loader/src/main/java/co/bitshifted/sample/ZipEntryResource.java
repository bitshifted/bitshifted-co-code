package co.bitshifted.sample;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.zip.ZipFile;

public class ZipEntryResource {

    public Resource getResource(String zipFilePath, String entryName) {
        try (var zipFile = new ZipFile(zipFilePath)) {
            var entry = zipFile.getEntry(entryName);
            var in = zipFile.getInputStream(entry);
            var resource = new ByteArrayResource(in.readAllBytes());
            in.close();
            return resource;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
