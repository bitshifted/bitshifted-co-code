package co.bitshifted.sample.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiGatewayEvent {
    private String httpMethod;
    private String path;
    private String body;
}
