package com.example.mysqlSpring.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class EmployeeDetails {
    @Id
    private String id; 
    private String name;
    private String designation;
    private String department;
    private String email;
    private String mobile;
    private String location;
    private String managerId;
    private String dateOfJoining;
    private Date createdTime;
    private Date updatedTime;
}
