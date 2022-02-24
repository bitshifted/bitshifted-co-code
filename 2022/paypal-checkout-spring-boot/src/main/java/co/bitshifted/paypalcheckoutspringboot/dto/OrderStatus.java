package co.bitshifted.paypalcheckoutspringboot.dto;

public enum OrderStatus {
    CREATED,
    SAVED,
    APPROVED,
    VOIDED,
    COMPLETED,
    PAYER_ACTION_REQUIRED;
}
