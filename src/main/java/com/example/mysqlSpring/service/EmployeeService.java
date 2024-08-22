package com.example.mysqlSpring.service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.mysqlSpring.model.*;
import com.example.mysqlSpring.repository.EmployeeRepository;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public ResponseEntity<EmployeeResponseUpdate> addEmployee(EmployeeDetails employee) {
        List<EmployeeDetails> employees=employeeRepository.findAll();

        //To check whether there is only 1 manager for a department
        if (employee.getDesignation().equalsIgnoreCase("Account Manager")) {
            for (EmployeeDetails emp : employees){
                if (emp.getDepartment().matches(employee.getDepartment())) {
                    EmployeeResponseUpdate employeeResponseUpdate=new EmployeeResponseUpdate("Department " + employee.getDepartment() + " already has a manager.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(employeeResponseUpdate);
                }
            }
        }

        //To check whether the adding employee have a manager or not
        List<EmployeeDetails> employeesOfDep=employeeRepository.findAllByDepartment(employee.getDepartment());
        if(employeesOfDep.isEmpty() && employee.getDesignation().matches("associate")){
            EmployeeResponseUpdate employeeResponseUpdate=new EmployeeResponseUpdate("Department " + employee.getDepartment() + " doesn't contain a manager.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(employeeResponseUpdate);
        }

        Date today = new Date();
        employee.setId(IdGenerator.generateId());
        employee.setCreatedTime(today);
        employee.setUpdatedTime(today);
        employeeRepository.save(employee);
        EmployeeResponseUpdate responseUpdate = new EmployeeResponseUpdate("Employee added successfully");
        return ResponseEntity.ok(responseUpdate);
    }

    public ResponseEntity<EmployeeResponseGet> getFilteredEmployees(Integer yearOfExperience, String managerId) {
        List<EmployeeDetails> employees=employeeRepository.findAll();

        //Creating a set with all manager id's
        Set<String> allManagerIds=new HashSet<>();
        for (EmployeeDetails employeeDetails : employees) {
            if ("Account Manager".equals(employeeDetails.getDesignation())) {
                allManagerIds.add(employeeDetails.getId());
            }
        }

        // group by managerid
        Map<String, List<EmployeeDetails>> employeesByManager = employees.stream()
        .filter(emp -> emp.getManagerId() != null) // Ensure no null keys
        .collect(Collectors.groupingBy(EmployeeDetails::getManagerId, Collectors.toList()));
        
        
        for (String mngId : allManagerIds) {
                employeesByManager.putIfAbsent(mngId, new ArrayList<>());
        }

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        // Create the filtered responses
        List<EmployeeResponse> filteredResponses = employeesByManager.entrySet().stream()
            .map(entry -> {
                String currentManagerId = entry.getKey();
                List<EmployeeDetails> employeeList = entry.getValue();

                Optional<EmployeeDetails> managerOpt = employeeRepository.findById(currentManagerId);
                String managerName = managerOpt.map(EmployeeDetails::getName).orElse("Unknown");
                String managerDept = managerOpt.map(EmployeeDetails::getDepartment).orElse("Unknown");

                // Filter employees based on condition
                List<EmployeeResponseDTO> filteredEmployeeList = employeeList.stream()
                    .filter(employee -> {
                        Date dateOfJoining = employee.getDateOfJoining();
                        if (dateOfJoining == null) return false; // Skip if dateOfJoining is null
            
                        LocalDateTime joiningDate = LocalDateTime.ofInstant(dateOfJoining.toInstant(), ZoneId.of("UTC"));
                        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
                        int yearsOfExperienceCalculated = (int) ChronoUnit.YEARS.between(joiningDate, now);

                        return (managerId == null || managerId.equalsIgnoreCase(currentManagerId)) &&
                            (yearOfExperience == null || yearsOfExperienceCalculated >= yearOfExperience);
                    }).map(emp->new EmployeeResponseDTO(
                        emp.getId(),
                        emp.getName(),
                        emp.getDesignation(),
                        emp.getDepartment(),
                        emp.getEmail(),
                        emp.getMobile(),
                        emp.getLocation(),
                        emp.getDateOfJoining(),
                        emp.getCreatedTime(),
                        emp.getUpdatedTime()
                    ))
                    .collect(Collectors.toList());

                // Include the manager in the response if conditions are met
                if ((managerId == null || managerId.equalsIgnoreCase(currentManagerId)) && Integer.parseInt(currentManagerId)>0 &&
                    (yearOfExperience == null || employeeList.stream()
                        .anyMatch(employee -> {
                            LocalDateTime joiningDate = LocalDateTime.ofInstant(employee.getDateOfJoining().toInstant(), ZoneId.of("UTC"));
                            LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
                            int yearsOfExperienceCalculated = (int) ChronoUnit.YEARS.between(joiningDate, now);
                            return yearsOfExperienceCalculated >= yearOfExperience;
                        }))) {
                    return new EmployeeResponse(
                        managerName,
                        managerDept,
                        currentManagerId,
                        filteredEmployeeList
                    );
                } else {
                    return null;
                }
            })
            .filter(Objects::nonNull) // Remove null responses
            .collect(Collectors.toList());
    
        String responseMessage = filteredResponses.isEmpty() ? "No employees found" : "Successfully fetched";
        EmployeeResponseGet response = new EmployeeResponseGet(responseMessage, filteredResponses);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<EmployeeResponseUpdate> updateEmployee(String id, String managerId) {

        List<EmployeeDetails> employeeDetailsList=employeeRepository.findAll();
        for(EmployeeDetails employeeDetails:employeeDetailsList){
            if(employeeDetails.getManagerId().matches(id)){
                EmployeeResponseUpdate response = new EmployeeResponseUpdate("Manager id cannot be provided as employee id");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
        }
        Optional<EmployeeDetails> employee=employeeRepository.findById(id);
        if(!employee.isPresent()){
            EmployeeResponseUpdate response = new EmployeeResponseUpdate("Employee not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Optional<EmployeeDetails> oldManager=employeeRepository.findById(employee.get().getManagerId());
        Optional<EmployeeDetails> newManager=employeeRepository.findById(managerId);

        if (!newManager.isPresent()) {
            EmployeeResponseUpdate response = new EmployeeResponseUpdate("Manager not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Date today=new Date();
        employee.get().setManagerId(managerId);
        employee.get().setUpdatedTime(today);
        employeeRepository.save(employee.get());
        EmployeeResponseUpdate employeeResponseUpdate=new EmployeeResponseUpdate(employee.get().getName()+ "'s manager has been successfully changed from " +oldManager.get().getName()+ " to " +newManager.get().getName());

        return ResponseEntity.ok(employeeResponseUpdate);
    }

    public ResponseEntity<EmployeeResponseUpdate> deleteEmployee(String id) {
        if(employeeRepository.existsById(id)){
            Optional<EmployeeDetails> employeeDetails=employeeRepository.findById(id);
            if(employeeDetails.isPresent()){
                EmployeeDetails emp=employeeDetails.get();
                if(emp.getDesignation().matches("Account Manager")){
                    if(employeeRepository.findAllByManagerId(id).isEmpty()){
                        employeeRepository.deleteById(id);
                        EmployeeResponseUpdate employeeResponseUpdate=new EmployeeResponseUpdate("Successfully deleted " +emp.getName()+ " from the employee list of the organization");
                        return ResponseEntity.ok(employeeResponseUpdate);
                    }
                    else{
                        EmployeeResponseUpdate employeeResponseUpdate=new EmployeeResponseUpdate("Cannot delete " +emp.getName()+ " because he manages other employees");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(employeeResponseUpdate);
                    }
                }
                else{
                    employeeRepository.deleteById(id);
                    EmployeeResponseUpdate employeeResponseUpdate=new EmployeeResponseUpdate("Successfully deleted " +emp.getName()+ " from the employee list of the organization");
                    return ResponseEntity.ok(employeeResponseUpdate);
                }
            }
            else{
                EmployeeResponseUpdate employeeResponseUpdate=new EmployeeResponseUpdate("Employee not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(employeeResponseUpdate);
            }
        }
        else{
            EmployeeResponseUpdate employeeResponseUpdate=new EmployeeResponseUpdate("Employee not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(employeeResponseUpdate);
        }
    }
}
