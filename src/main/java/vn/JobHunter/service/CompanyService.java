package vn.JobHunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.JobHunter.domain.Company;
import vn.JobHunter.domain.User;
import vn.JobHunter.domain.respone.ResultPaginationDto;
import vn.JobHunter.repository.CompanyRepository;
import vn.JobHunter.repository.UserRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleCreateCompany(Company company) {
        Company cmp = this.companyRepository.save(company);
        return cmp;
    }

    public ResultPaginationDto fetchAllCompanies(Pageable pageable, Specification<Company> spec) {
        Page<Company> companies = this.companyRepository.findAll(spec, pageable);

        ResultPaginationDto res = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
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
        List<User> users = userRepository.findByCompanyId(id);
        this.userRepository.deleteAll(users);

        this.companyRepository.deleteById(id);
    }

    public List<User> getAllUserByCompany(Long id) {
        List<User> users = this.userRepository.findByCompanyId(id);
        return users;
    }

}
