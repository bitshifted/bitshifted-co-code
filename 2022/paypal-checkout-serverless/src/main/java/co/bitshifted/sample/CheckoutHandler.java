package co.bitshifted.sample;

import co.bitshifted.sample.config.PaypalConfig;
import co.bitshifted.sample.dto.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

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
                // write to DynamoDB
                var dbClient = DynamoDbClient.builder().region(Region.EU_CENTRAL_1).build();
                var itemMap = new HashMap<String, AttributeValue>();
                itemMap.put("paypalTxId", AttributeValue.builder().s(orderResponse.getId()).build());
                itemMap.put("status", AttributeValue.builder().s(orderResponse.getStatus().toString()).build());
                itemMap.put("timestamp", AttributeValue.builder().s(ZonedDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_DATE_TIME)).build());
                System.out.println("Item map: " + itemMap);
                var putRequest = PutItemRequest.builder()
                    .tableName("TxTable")
                    .item(itemMap)
                    .build();
                dbClient.putItem(putRequest);
                System.out.println("Successfully created order");
                return ApiGatewayResponse.builder().statusCode(200)
                    .body(mapper.writeValueAsString(orderResponse))
                    .build();
            } else if(isOrderSuccessRequest(event)) {
                var token = event.getQueryStringParameters().get("token");
                // update order status
                var key = new HashMap<String, AttributeValue>();
                key.put("paypalTxId", AttributeValue.builder().s(token).build());

                var updateValues = new HashMap<String, AttributeValueUpdate>();
                updateValues.put("status", AttributeValueUpdate.builder().value(
                    AttributeValue.builder().s(OrderStatus.COMPLETED.toString()).build()).build());
                updateValues.put("timestamp", AttributeValueUpdate.builder().value(
                    AttributeValue.builder().s(ZonedDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_DATE_TIME)).build()).build());

                var dbClient = DynamoDbClient.builder().region(Region.EU_CENTRAL_1).build();
                var request = UpdateItemRequest.builder()
                    .tableName("TxTable")
                        .key(key)
                            .attributeUpdates(updateValues)
                                .build();
                dbClient.updateItem(request);

                return ApiGatewayResponse.builder()
                    .statusCode(200)
                    .body("{\"status\": \"success\"}")
                    .build();
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
