package com.vivatech.biller.apiprocessor.admasdto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdCardTypeMap {
    private Map<String, Double> idCardTypeMap;
}
