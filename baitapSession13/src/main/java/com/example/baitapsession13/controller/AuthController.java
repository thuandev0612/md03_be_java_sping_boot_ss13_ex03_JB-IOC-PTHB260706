package com.example.baitapsession13.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @RequestMapping(value = "/test", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, String> test() {
        return Map.of("message", "API public truy cập thành công");
    }
}
