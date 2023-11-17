package com.vivatech.biller.apiprocessor.admasdto;

import lombok.Data;

@Data
public class AdmasPaymentResponse {
    private String result;
    private String message;
    private String receiptNo;
    private String registrationNo;
}
