package vn.JobHunter.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.JobHunter.domain.Company;
import vn.JobHunter.service.CompanyService;
import vn.JobHunter.util.annotation.ApiMessage;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> handleCreateComapny(@Valid @RequestBody Company company) {

        Company cmp = this.companyService.handleCreateCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(cmp);
    }

    @GetMapping("/companies")
    @ApiMessage("Fetch all companies")
    public ResponseEntity<List<Company>> handleGetAllComapny() {
        List<Company> companies = this.companyService.fetchAllCompanies();
        return ResponseEntity.ok().body(companies);
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> handleGetCompanyById(@PathVariable("id") Long id) {
        Company c = this.companyService.fetchCompanyById(id);
        return ResponseEntity.ok().body(c);
    }

    @PutMapping("companies/{id}")
    public ResponseEntity<Company> handleUpdateCompany(@PathVariable("id") Long id, @RequestBody Company c) {
        Company company = this.companyService.fetchCompanyById(id);
        if (company != null) {
            company.setAddress(c.getAddress());
            company.setName(c.getName());
            company.setDescription(c.getDescription());
            company.setLogo(c.getName());
            company = this.companyService.handleCreateCompany(company);
        }

        return ResponseEntity.ok().body(company);
    }

}
