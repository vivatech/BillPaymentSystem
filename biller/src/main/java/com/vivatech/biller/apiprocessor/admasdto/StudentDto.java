package com.vivatech.biller.apiprocessor.admasdto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentDto {
    private Integer id;
    private String firstName;
    private String contactNo;
    private String email = null;
    private Integer allottedDepartment;
    private String allottedDepartmentName;
    private String status;
    private String registrationNo;
    private String admissionNo;
    private Integer semesterid;
    private String semestername;
}
