package com.vivatech.biller.apiprocessor.admasdto;

import lombok.Data;

import java.util.List;

@Data
public class AdmasFeePaymentRequest {
    private PaymentDto paymentDto;
    private List<FeePayment> feePaymentList;
}
