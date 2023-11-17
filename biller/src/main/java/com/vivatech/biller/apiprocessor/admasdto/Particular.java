package com.vivatech.biller.apiprocessor.admasdto;

import lombok.Data;

@Data
public class Particular {
    private String particularTitle;
    private Double amount;
    private Double exemption;
}
