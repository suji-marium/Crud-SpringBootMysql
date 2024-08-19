package com.example.mysqlSpring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mysqlSpring.model.EmployeeDetails;

public interface MysqlRepo extends JpaRepository<EmployeeDetails,String> {
    List<EmployeeDetails> findAllByManagerId(String managerId);
    List<EmployeeDetails> findAllByDepartment(String department);
}
