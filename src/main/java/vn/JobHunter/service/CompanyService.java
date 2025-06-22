package vn.JobHunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.JobHunter.domain.Company;
import vn.JobHunter.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleCreateCompany(Company company) {
        Company cmp = this.companyRepository.save(company);
        return cmp;
    }

    public List<Company> fetchAllCompanies() {
        List<Company> companies = this.companyRepository.findAll();
        return companies;
    }

    public Company fetchCompanyById(Long id) {
        Optional<Company> c = this.companyRepository.findById(id);
        return c.isPresent() ? c.get() : null;
    }

}
