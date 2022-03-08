package co.bitshifted.sample;

import co.bitshifted.sample.config.PaypalConfig;
import co.bitshifted.sample.dto.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CheckoutHandler implements RequestHandler<ApiGatewayEvent, ApiGatewayResponse> {
    @Override
    public ApiGatewayResponse handleRequest(ApiGatewayEvent event, Context context) {
        var mapper = new ObjectMapper();
        var config = new PaypalConfig();
        config.setBaseUrl(System.getenv("PAYPAL_BASE_URL"));
        config.setClientId(System.getenv("PAYPAL_APP_ID"));
        config.setSecret(System.getenv("PAYPAL_SECRET"));
        System.out.println("Config: " + config);
        System.out.println("API event: " + event.toString());



        try {
            if(isCreateOrderRequqest(event)) {
                var client = new PayPalHttpClient(config, new ObjectMapper());

                var appContext = new PayPalAppContextDTO();
                appContext.setReturnUrl("https://pu8y9pi5e1.execute-api.eu-central-1.amazonaws.com/dev/checkout/success");
                appContext.setBrandName("My brand");
                appContext.setLandingPage(PaymentLandingPage.BILLING);

                var order = mapper.readValue(event.getBody(), OrderDTO.class);
                order.setApplicationContext(appContext);
                var orderResponse = client.createOrder(order);
                return ApiGatewayResponse.builder().statusCode(200)
                    .body(mapper.writeValueAsString(orderResponse))
                    .build();
            } else if(isOrderSuccessRequest(event)) {

            }
            return ApiGatewayResponse.builder()
                .statusCode(400)
                .body("Invaid request")
                .build();

        } catch (Exception ex) {
            ex.printStackTrace();
            return ApiGatewayResponse.builder()
                .statusCode(500)
                .body(ex.getMessage())
                .build();
        }

    }

    private boolean isCreateOrderRequqest(ApiGatewayEvent event) {
        return "POST".equalsIgnoreCase(event.getHttpMethod()) &&
            "/checkout".equalsIgnoreCase(event.getPath());
    }

    private boolean isOrderSuccessRequest(ApiGatewayEvent event) {
        return "GET".equalsIgnoreCase(event.getHttpMethod()) &&
            "/checkout/success".equalsIgnoreCase(event.getPath());
    }
}
