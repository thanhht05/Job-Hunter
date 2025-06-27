package vn.JobHunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.JobHunter.domain.Company;
import vn.JobHunter.domain.dto.Meta;
import vn.JobHunter.domain.dto.ResultPaginationDto;
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

    public ResultPaginationDto fetchAllCompanies(Pageable pageable, Specification<Company> spec) {
        Page<Company> companies = this.companyRepository.findAll(spec, pageable);

        ResultPaginationDto res = new ResultPaginationDto();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(companies.getTotalPages());
        meta.setTotalElements(companies.getTotalElements());
        res.setMeta(meta);
        res.setResult(companies.getContent());

        return res;
    }

    public Company fetchCompanyById(Long id) {
        Optional<Company> c = this.companyRepository.findById(id);
        return c.isPresent() ? c.get() : null;
    }

    public void deleteCompany(Long id) {
        this.companyRepository.deleteById(id);
    }

}
