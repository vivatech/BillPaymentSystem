package com.vivatech.biller.apiprocessor.admasdto;

import lombok.Data;

@Data
public class PaySlipDescription {
    private Double previousSemesterFee;
    private Double semesterFee;
    private Double droppedCourseFee;
    private Double scholarShipFee;
    private Double discountFee;
    private Double partialPayment;
    private Double additionalCourseFee;
    private Double penaltyFee;
}
