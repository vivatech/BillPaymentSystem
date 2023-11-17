package com.vivatech.biller.apiprocessor.admasdto;

import lombok.Data;

@Data
public class PaymentDto {
    private String orderId;
    private double bankReceiptAmount;
    private String paymentMethod;
    private String registrationNo;
    private long semesterId;
    private long departmentId;
    private String paymentMode;
    private String timeStamp;
    private StudentDto studentDto;
}
