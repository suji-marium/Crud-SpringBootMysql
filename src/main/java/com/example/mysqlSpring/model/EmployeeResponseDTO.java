package com.example.mysqlSpring.model;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDTO {
    private String id; 
    private String name;
    private String designation;
    private String department;
    private String email;
    private String mobile;
    private String location;
    private Date dateOfJoining;
    private Date createdTime;
    private Date updatedTime;
}
