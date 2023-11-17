package com.vivatech.biller.controller;

import com.vivatech.biller.apiprocessor.BillerApiProcessor;
import com.vivatech.biller.dto.BillRequest;
import com.vivatech.biller.dto.PaymentResponse;
import com.vivatech.biller.exception.PaymentAppException;
import com.vivatech.biller.model.Biller;
import com.vivatech.biller.repository.BillerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@Slf4j
@RequestMapping(path = "/bill")
public class BillPaymentController {

    @Autowired
    private BillerRepository billerRepository;

    @Autowired
    private BillerApiProcessor billerApiProcessor;

    @RequestMapping(value = "/get-due-bill", method = RequestMethod.POST)
    public PaymentResponse getDueBill(@RequestBody BillRequest billRequest){
        log.info("REQUEST: {}", billRequest);
        Biller biller = billerRepository.findById(billRequest.getBillerId()).orElse(null);
        if (biller == null) throw new PaymentAppException("Invalid biller.");
        PaymentResponse dueBill = billerApiProcessor.getDueBill(billRequest.getConsumerNo(), biller.getBillerName());
        log.info("RESPONSE: {}", dueBill);
        return dueBill;
    }

}
