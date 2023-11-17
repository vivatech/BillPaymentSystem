package com.vivatech.biller.apiprocessor.admasdto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdmasBillerResponse {
    private StudentDto studentDto;
    private Map<String, Double> idCardTypeMap;
    private String feeType;
    private Double feeAmount;
    private String studentType;
    private PaymentSlipDto paymentSlipDto;
    private PaySlipDescription paySlipDescription;
    private Boolean registrationPeriod;
}
