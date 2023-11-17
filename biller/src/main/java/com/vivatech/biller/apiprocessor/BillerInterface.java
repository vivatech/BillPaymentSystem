package com.vivatech.biller.apiprocessor;

import com.vivatech.biller.dto.BillPaymentResponse;
import com.vivatech.biller.dto.DueBillResponse;

public interface BillerInterface {
    boolean supports(String biller);
    DueBillResponse getBillDetail(String consumerNo);
    BillPaymentResponse payBill(String customerNo, String requestDto);
}
