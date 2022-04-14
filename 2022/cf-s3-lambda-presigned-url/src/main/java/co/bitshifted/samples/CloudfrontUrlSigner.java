package co.bitshifted.samples;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CloudfrontUrlSigner {

    private static final String RESOURCE_PATH_FORMAT = "https://%s/%s";
    private static final String URL_FORMAT = "https://%s/%s?Expires=%d&Signature=%s&Key-Pair-Id=%s";

    private String cannedPolicy;
    private final SecureRandom srand;

    public CloudfrontUrlSigner() {
        srand = new SecureRandom();
        try {
            var policyPath = Paths.get(getClass().getResource("/canned-policy.json").toURI());
            cannedPolicy = Files.readString(policyPath);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String sign(String path, Duration duration, PrivateKey privateKey, String domain, String keyPairId) throws Exception {
        var expiration = getExpiration(duration);
        var resourcePath = String.format(RESOURCE_PATH_FORMAT, domain, path);
        var policy = buildCannedPolicy(resourcePath, expiration);
        System.out.println("Policy: " + policy);
        byte[] signatureBytes = signWithSha1Rsa(policy.getBytes(StandardCharsets.UTF_8), privateKey);
        String urlSafeSignature = makeBytesUrlSafe(signatureBytes);
        var url = String.format(URL_FORMAT, domain, path, expiration, urlSafeSignature, keyPairId);
        System.out.println("Signed URL: " + url);
        return url;
    }

    private long getExpiration(Duration duration) {
        return ZonedDateTime.now(ZoneId.of("UTC")).plus(duration).toInstant().getEpochSecond();
    }

    private String buildCannedPolicy(String resourcePath, long expires) {
        var policy = String.format(cannedPolicy, resourcePath, expires).replaceAll("\\s+", "");
        return policy;
    }

    private byte[] signWithSha1Rsa(byte[] dataToSign,
                                         PrivateKey privateKey) throws InvalidKeyException {
        Signature signature;
        try {
            signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(privateKey, srand);
            signature.update(dataToSign);
            return signature.sign();
        } catch (NoSuchAlgorithmException | SignatureException e) {
            throw new IllegalStateException(e);
        }
    }

    private String makeBytesUrlSafe(byte[] bytes) {
        byte[] encoded = java.util.Base64.getEncoder().encode(bytes);

        for (int i = 0; i < encoded.length; i++) {
            switch (encoded[i]) {
                case '+':
                    encoded[i] = '-';
                    continue;
                case '=':
                    encoded[i] = '_';
                    continue;
                case '/':
                    encoded[i] = '~';
                    continue;
                default:
                    continue;
            }
        }
        return new String(encoded, StandardCharsets.UTF_8);
    }

}
