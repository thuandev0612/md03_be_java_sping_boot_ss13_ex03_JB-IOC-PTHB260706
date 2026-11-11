package com.example.baitapsession13.controller;

import com.example.baitapsession13.model.Employee;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    @GetMapping
    public List<Employee> findAll() {
        return List.of(
                new Employee(1L, "Nguyen Van An", 10_000_000L),
                new Employee(2L, "Tran Thi Binh", 12_000_000L),
                new Employee(3L, "Le Van Cuong", 15_000_000L)
        );
    }
}
