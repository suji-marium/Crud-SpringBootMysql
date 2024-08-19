package com.example.mysqlSpring.repository;

import java.util.List;
import java.util.Optional;

import com.example.mysqlSpring.model.EmployeeDetails;


public interface EmployeeRepository{
    public List<EmployeeDetails> findAllByManagerId(String managerId);

    public void save(EmployeeDetails employeeDetails);

    public List<EmployeeDetails> findAll();

    public Optional<EmployeeDetails> findById(String currentManagerId);

    public void deleteById(String id);

    public boolean existsById(String id);

    public List<EmployeeDetails> findAllByDepartment(String department);
}
