package co.bitshifted.sample.dto;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class ApiGatewayResponse {
    private int statusCode;
    private Map<String, String> headers = new HashMap<>();
    private String body;
}
