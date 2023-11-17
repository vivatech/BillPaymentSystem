package com.vivatech.biller.apiprocessor;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivatech.biller.apiprocessor.admasdto.*;
import com.vivatech.biller.dto.BillPaymentResponse;
import com.vivatech.biller.dto.CustomerDto;
import com.vivatech.biller.dto.PaymentResponse;
import com.vivatech.biller.exception.PaymentAppException;
import com.vivatech.biller.model.BillerTransaction;
import com.vivatech.biller.repository.BillerRepository;
import com.vivatech.biller.repository.BillerTransactionRepository;
import com.vivatech.biller.utility.PaymentEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private BillerRepository billerRepository;

    @Autowired
    private BillerTransactionRepository transactionRepository;

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

    @Transactional(rollbackFor = {Exception.class, JsonParseException.class, IOException.class})
    @Override
    public BillPaymentResponse payBill(String customerNo, String requestDto){
        BillPaymentResponse paymentResponse = new BillPaymentResponse();
        try {
            String url = endpoint + "transaction/create-offline-payment";
            ObjectMapper objectMapper = new ObjectMapper();
            AdmasFeePaymentRequest admasFeePaymentRequest = objectMapper.readValue(requestDto, AdmasFeePaymentRequest.class);
            PaymentDto paymentDto = admasFeePaymentRequest.getPaymentDto();
            StudentDto studentDto = new StudentDto();
            studentDto.setRegistrationNo(paymentDto.getRegistrationNo());
            paymentDto.setStudentDto(studentDto);
            admasFeePaymentRequest.setPaymentDto(paymentDto);
            BillerTransaction transaction = convertAdmasDtoToBillerTransactionDto(admasFeePaymentRequest);
            BillerTransaction savedStartedTransaction = transactionRepository.save(transaction);
            log.info("Sending request to admas for Registration No: {} and request body : {}", customerNo, new ObjectMapper().writeValueAsString(admasFeePaymentRequest));
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("API-Key", admasKey);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(new ObjectMapper().writeValueAsString(admasFeePaymentRequest)));
            CloseableHttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                String errorBody = getResponseString(response);
                log.error("Error response: {}", errorBody);
                paymentResponse.setStatus("Failed");
                paymentResponse.setMessage(errorBody);
                savedStartedTransaction.setStatus(String.valueOf(PaymentEnum.ResponseStatus.FAILED));
                savedStartedTransaction.setReason(errorBody);
                transactionRepository.save(savedStartedTransaction);
                return paymentResponse;
            }
            String resBody = getResponseString(response);
            log.info("Payment response for Registration No: {} is {}", customerNo, resBody);
            AdmasPaymentResponse admasPaymentResponse = objectMapper.readValue(resBody, AdmasPaymentResponse.class);
            if (admasPaymentResponse.getResult().equalsIgnoreCase(String.valueOf(PaymentEnum.ResponseStatus.SUCCESS))){
                paymentResponse.setResponseTransactionId(admasPaymentResponse.getReceiptNo());
                paymentResponse.setStatus(admasPaymentResponse.getResult());
                paymentResponse.setRequestTransactionId(admasFeePaymentRequest.getPaymentDto().getOrderId());
                savedStartedTransaction.setStatus(String.valueOf(PaymentEnum.ResponseStatus.SUCCESS));
                savedStartedTransaction.setResponseTransactionId(admasPaymentResponse.getReceiptNo());
                transactionRepository.save(savedStartedTransaction);
            }
        } catch (Exception e){
            log.error("Exception: ", e);
        }
        return paymentResponse;
    }

    private BillerTransaction convertAdmasDtoToBillerTransactionDto(AdmasFeePaymentRequest request){
        BillerTransaction transaction = new BillerTransaction();
        BillerTransaction existingTransaction = transactionRepository.findByTransactionId(request.getPaymentDto().getOrderId());
        if (existingTransaction != null && !existingTransaction.getStatus().equalsIgnoreCase(String.valueOf(PaymentEnum.ResponseStatus.FAILED))) throw new PaymentAppException("Bill already in progress");
        transaction.setTransactionId(request.getPaymentDto().getOrderId());
        transaction.setBiller(billerRepository.findByBillerName(StringUtils.replace(String.valueOf(PaymentEnum.BillerName.ADMAS_UNIVERSITY), "_", " ")));
        transaction.setAmount(request.getPaymentDto().getBankReceiptAmount());
        transaction.setStatus(String.valueOf(PaymentEnum.ResponseStatus.STARTED));
        return transaction;
    }
}
