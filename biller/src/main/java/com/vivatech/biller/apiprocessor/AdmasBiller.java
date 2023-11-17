package com.vivatech.biller.apiprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivatech.biller.apiprocessor.admasdto.AdmasBillerResponse;
import com.vivatech.biller.dto.CustomerDto;
import com.vivatech.biller.dto.PaymentResponse;
import com.vivatech.biller.utility.PaymentEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;


@Service
@Slf4j
public class AdmasBiller implements BillerInterface{

    @Value("${admas.api.endpoint}")
    private String endpoint;

    @Value("${admas.api.apikey}")
    private String admasKey;

    @Override
    public boolean supports(String biller) {
        return biller.equalsIgnoreCase(StringUtils.replace(String.valueOf(PaymentEnum.BillerName.ADMAS_UNIVERSITY), "_", " "));
    }

    @Override
    public PaymentResponse getBillDetail(String registrationNo) {
        PaymentResponse paymentResponse = new PaymentResponse();
        log.info("posting request to admas university for due payment of student id: {}", registrationNo);
        try {
            String url = endpoint + "payment/get-payment-detail-by-fee-type";

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("API-Key", admasKey);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(registrationNo));
//            log.info("Sending request body to admas biller: {}", new ObjectMapper().writeValueAsString(httpPost));
            CloseableHttpResponse response = client.execute(httpPost);


            String resBody = getResponseString(response);

            ObjectMapper objectMapper = new ObjectMapper();
            log.info("bill query request for admas university student id {} is {}", registrationNo, objectMapper.writeValueAsString(resBody));
            AdmasBillerResponse admasBillerResponse = objectMapper.readValue(resBody, AdmasBillerResponse.class);
            CustomerDto customerDto = new CustomerDto();
            customerDto.setId(admasBillerResponse.getStudentDto().getId());
            customerDto.setName(admasBillerResponse.getStudentDto().getFirstName());
            customerDto.setContact(admasBillerResponse.getStudentDto().getContactNo());
            paymentResponse.setCustomerDto(customerDto);
            paymentResponse.setAdmasFeeMap(admasBillerResponse.getIdCardTypeMap());
        } catch (Exception e){
            log.error("Exception: {}", e.getMessage(), e);
        }
        return paymentResponse;
    }

    private String getResponseString(CloseableHttpResponse response) throws IOException {
        String encoding = "UTF-8";
        InputStream in = response.getEntity().getContent();
        String resBody = IOUtils.toString(in, encoding);
        return resBody;
    }
}
