package co.bitshifted.samples;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class PrivateKeyHandlerTest {
    @Test
    public void testKey()  throws Exception{
        var path = Path.of("keys", "private_key_pkcs8.pem");
        var content = Files.readString(path);
        System.out.println("Content: " + content);
        var key = PrivateKeyHandler.readKey(content);
        System.out.println("Key: " + key);
    }
}
