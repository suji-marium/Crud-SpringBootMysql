package com.example.mysqlSpring.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.mysqlSpring.repository.EmployeeRepository;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IdGenerator {

    private static final AtomicInteger counter = new AtomicInteger(1);

    @Autowired
    public IdGenerator(EmployeeRepository employeeRepo) {
        initializeCounter(employeeRepo);
    }

    private static void initializeCounter(EmployeeRepository employeeRepo) {
        Optional<String> maxIdOpt = employeeRepo.findAll().stream()
                .map(EmployeeDetails::getId)
                .map(Integer::parseInt)
                .max(Integer::compareTo)
                .map(String::valueOf);

        int maxId = maxIdOpt.map(Integer::parseInt).orElse(0);
        counter.set(maxId);
    }

    public static String generateId() {
        return String.valueOf(counter.incrementAndGet());
    }
}
