package co.bitshifted.paypalcheckoutspringboot.dto;

import lombok.Data;

@Data
public class LinkDTO {
    private String href;
    private String rel;
    private String method;
}
