package com.vivatech.biller.dto;

import lombok.Data;

@Data
public class BillRequest {
    private Integer billerId;
    private String consumerNo;
    private String billPaymentRequestDto;
    private Double billAmount;
}
