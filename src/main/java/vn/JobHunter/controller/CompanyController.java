package vn.JobHunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.JobHunter.domain.Company;
import vn.JobHunter.domain.dto.CompanyDto;
import vn.JobHunter.domain.respone.ResultPaginationDto;
import vn.JobHunter.service.CompanyService;
import vn.JobHunter.util.annotation.ApiMessage;
import vn.JobHunter.util.exception.IdInvalidException;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    @ApiMessage("Create company")
    public ResponseEntity<Company> handleCreateComapny(@Valid @RequestBody Company company) {

        Company cmp = this.companyService.handleCreateCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(cmp);
    }

    @GetMapping("/companies")
    @ApiMessage("Fetch all companies")
    public ResponseEntity<ResultPaginationDto> handleGetAllComapny(@Filter Specification<Company> spec,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.companyService.fetchAllCompanies(pageable, spec));
    }

    @GetMapping("/companies/{id}")
    @ApiMessage("Get company")
    public ResponseEntity<CompanyDto> handleGetCompanyById(@PathVariable("id") Long id) {
        Company c = this.companyService.fetchCompanyById(id);
        CompanyDto companyDto = this.companyService.convertCompanyToCompanyDto(c);
        return ResponseEntity.ok().body(companyDto);
    }

    @PutMapping("/companies")
    @ApiMessage("Update company")
    public ResponseEntity<Company> handleUpdateCompany(@RequestBody Company c) {
        Company company = this.companyService.fetchCompanyById(c.getId());
        if (company != null) {
            company.setAddress(c.getAddress());
            company.setName(c.getName());
            company.setDescription(c.getDescription());
            company.setLogo(c.getName());
            company = this.companyService.handleCreateCompany(company);
        }

        return ResponseEntity.ok().body(company);
    }

    @DeleteMapping("/companies/{id}")
    @ApiMessage("Delete company")
    public ResponseEntity<Void> handleDeleteCompany(@PathVariable("id") Long id) throws IdInvalidException {
        Company c = this.companyService.fetchCompanyById(id);
        if (c == null) {
            throw new IdInvalidException("Company khong ton tai");
        }
        this.companyService.deleteCompany(id);
        return ResponseEntity.ok().body(null);
    }

}
