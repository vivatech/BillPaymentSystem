package com.vivatech.biller.apiprocessor;

import com.vivatech.biller.dto.BillPaymentResponse;
import com.vivatech.biller.dto.PaymentResponse;

public interface BillerInterface {
    boolean supports(String biller);
    PaymentResponse getBillDetail(String consumerNo);
    BillPaymentResponse payBill(String customerNo, String requestDto);
}
