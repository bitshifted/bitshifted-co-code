
package co.bitshifted.sample.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaypalConfig {
    private String baseUrl;
    private String clientId;
    private String secret;
}
