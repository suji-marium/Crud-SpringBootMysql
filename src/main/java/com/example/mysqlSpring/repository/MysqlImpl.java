package com.example.mysqlSpring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.mysqlSpring.model.EmployeeDetails;

@Repository
public class MysqlImpl implements EmployeeRepository{

    @Autowired
    MysqlRepo mysqlRepo;
    @Override
    public List<EmployeeDetails> findAllByManagerId(String managerId) {
        return mysqlRepo.findAllByManagerId(managerId);
    }

    @Override
    public void save(EmployeeDetails employeeDetails) {
        mysqlRepo.save(employeeDetails);
    }

    @Override
    public List<EmployeeDetails> findAll() {
        return mysqlRepo.findAll();
    }

    @Override
    public Optional<EmployeeDetails> findById(String currentManagerId) {
        return mysqlRepo.findById(currentManagerId);
    }

    @Override
    public void deleteById(String id) {
        mysqlRepo.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return mysqlRepo.existsById(id);
    }

    @Override
    public List<EmployeeDetails> findAllByDepartment(String department) {
        return mysqlRepo.findAllByDepartment(department);
    }
    
}
