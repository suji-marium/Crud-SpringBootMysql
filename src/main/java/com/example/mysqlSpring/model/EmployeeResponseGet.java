package com.example.mysqlSpring.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseGet {
    private String message;
    private List<EmployeeResponse> details;
}
