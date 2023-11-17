package com.vivatech.biller.dto;

import lombok.Data;

@Data
public class BillPaymentResponse {
    private String status;
    private String message;
    private String requestTransactionId;
    private String responseTransactionId;
}
