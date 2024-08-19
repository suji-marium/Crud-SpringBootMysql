package com.example.mysqlSpring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mysqlSpring.model.EmployeeDetails;
import com.example.mysqlSpring.model.EmployeeResponseGet;
import com.example.mysqlSpring.model.EmployeeResponseUpdate;
import com.example.mysqlSpring.service.EmployeeService;

@RestController
@RequestMapping("/api/mysql/")
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;

    @PostMapping("/addEmployee")
    public ResponseEntity<EmployeeResponseUpdate> addEmployee(@RequestBody EmployeeDetails employeeDetails){
        return employeeService.addEmployee(employeeDetails);
    }

    @GetMapping("/viewEmployee")
    public ResponseEntity<EmployeeResponseGet> getFilteredEmployees(
        @RequestParam(value = "year-of-experience", required = false) Integer yearOfExperience,
        @RequestParam(value = "managerId", required = false) String managerId) {
                return employeeService.getFilteredEmployees(yearOfExperience, managerId);
    }

    @PutMapping("/updateEmployee")
    public ResponseEntity<EmployeeResponseUpdate> updateEmployee(
        @RequestParam(value = "employeeId") String id,
        @RequestParam(value = "managerId") String managerId){
            return employeeService.updateEmployee(id,managerId);
        }

    @DeleteMapping("/deleteEmployee")
    public ResponseEntity<EmployeeResponseUpdate> deleteEmployee(
        @RequestParam (value = "employeeId") String id
    ){
        return employeeService.deleteEmployee(id);
    }
}
