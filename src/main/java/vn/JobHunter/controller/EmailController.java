package vn.JobHunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.JobHunter.service.EmailService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/email")
    public String getMethodName() {
        // this.emailService.sendEmailSync("huuthanh9agm@gmail.com", "test", "<h1> Get
        // out my head</h1>", false, true);
        this.emailService.sendEmailFromTemplateSync("huuthanh2005qt@gmail.com", "Get your job", "job");
        return "ok";
    }
}
