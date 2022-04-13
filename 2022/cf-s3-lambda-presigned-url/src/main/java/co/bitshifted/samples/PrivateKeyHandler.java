package co.bitshifted.samples;

import java.io.BufferedReader;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class PrivateKeyHandler {

    public static PrivateKey readKey(String pem)  throws Exception{
        var sb = new StringBuilder();
        try (var reader = new BufferedReader(new StringReader(pem))) {
            String line;
            while((line = reader.readLine()) != null) {
                if(!line.contains("PRIVATE KEY")) {
                    sb.append(line);
                }
            }
        }
        var key = sb.toString();
        System.out.println("Key: " + key);
        byte[] encoded = Base64.getDecoder().decode(key);
        var factory = KeyFactory.getInstance("RSA");
        var keySpec = new PKCS8EncodedKeySpec(encoded);
        return factory.generatePrivate(keySpec);
    }
}
