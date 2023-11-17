package com.vivatech.biller.apiprocessor.admasdto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentSlipDto {
    private StudentDto studentDto;
    private Double semesterFee;
    private Double additionCourseFee;
    private Double removedCourseFee;
    private Double scholarshipFee;
    private Double sponsorshipFee;
    private Double lateFee;
    private Double penaltyFee;
    private Double discountFee;
    private List<Particular> particularList;
    private Double partialPayment;
    private String errorMsg;
}
