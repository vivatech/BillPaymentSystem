package com.vivatech.biller.controller;

import com.vivatech.biller.exception.PaymentAppException;
import com.vivatech.biller.dto.Response;
import com.vivatech.biller.model.Biller;
import com.vivatech.biller.repository.BillerRepository;
import com.vivatech.biller.utility.PaymentEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@Slf4j
@RequestMapping(path = "/biller")
public class BillerController {

    @Autowired
    private BillerRepository billerRepository;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Response createBiller(@RequestBody Biller biller) {
        Biller existingBiller = billerRepository.findByBillerName(biller.getBillerName());
        if (existingBiller != null) throw new PaymentAppException("Biller already exist.");
        Biller newBiller = new Biller();
        newBiller.setBillerName(biller.getBillerName());
        newBiller.setBillerCategory(biller.getBillerCategory());
        billerRepository.save(biller);
        return Response.builder().result(String.valueOf(PaymentEnum.ResponseStatus.SUCCESS)).message("Biller successfully created").build();
    }
}
