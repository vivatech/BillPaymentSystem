package com.vivatech.biller.apiprocessor.admasdto;

import lombok.Data;

@Data
public class FeePayment {
    private String feeType;
    private double grossFee;
    private double paidFee;
    private double netFee;
    private double balance;
    private double discount;
    private double exemptedFee;
    private double removedCourseFee;
    private double previousSemesterFee;
}
