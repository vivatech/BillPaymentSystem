package com.vivatech.biller.apiprocessor;

import com.vivatech.biller.dto.PaymentResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillerApiProcessor {

    private final List<BillerInterface> sortedProcessors;

    public BillerApiProcessor(List<BillerInterface> sortedProcessors) {
        this.sortedProcessors = sortedProcessors;
    }

    public BillerInterface getMatchedProcessor(String feeType) {
        for (BillerInterface processor : sortedProcessors) {
            if(processor.supports(feeType)) {
                return processor;
            }
        }
        return null;
    }

    public PaymentResponse getDueBill(String regNo, String billerName){
        BillerInterface matchedProcessor = getMatchedProcessor(billerName);
        return matchedProcessor.getBillDetail(regNo);
    }
}
