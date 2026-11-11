package com.example.baitapsession13.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Employee {
    private final Long id;
    private final String fullName;
    private final Long salary;
}
