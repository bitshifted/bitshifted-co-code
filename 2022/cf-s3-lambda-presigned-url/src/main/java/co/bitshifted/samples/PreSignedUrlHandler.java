package co.bitshifted.samples;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import java.io.BufferedReader;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;

/**
 * Hello world!
 *
 */
public class PreSignedUrlHandler implements RequestHandler<InputData, String> {

    private static final String PRIVATE_KEY_SECRET_VAR_NAME = "PRIVATE_KEY_SECRET_NAME";
    private static final String CF_DISTRO_DOMAIN_VAR_NAME = "CF_DISTRO_BASE_URL";
    private static final String KEYPAIR_ID_VAR_NAME = "CF_KEYPAIR_ID";

    @Override
    public String handleRequest(InputData input, Context context) {
        System.out.println("Input path: " + input.getPath());
        var privateKeySecretName = System.getenv(PRIVATE_KEY_SECRET_VAR_NAME);
        System.out.println("secret name: " + privateKeySecretName);
        var domainName = System.getenv(CF_DISTRO_DOMAIN_VAR_NAME);
        System.out.println("Distro domain: " + domainName);
        var keypairId = System.getenv(KEYPAIR_ID_VAR_NAME);
        System.out.println("Keypair ID: " + keypairId);
        var client = SecretsManagerClient.builder().region(Region.EU_CENTRAL_1).build();

        var secretRequest = GetSecretValueRequest.builder().secretId(privateKeySecretName).build();
        var secretResponse = client.getSecretValue(secretRequest);
        System.out.println("Secret response: \n" + secretResponse);
        try {
            var privKey = PrivateKeyHandler.readKey(secretResponse.secretString());
            var signer = new CloudfrontUrlSigner();
            signer.sign(input.getPath(), Duration.ofDays(7), privKey, domainName, keypairId);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }


        return "hello";
    }
}


