package vn.JobHunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    @GetMapping("/")
    String getPage() {
        return "hello 123";
    }
}
