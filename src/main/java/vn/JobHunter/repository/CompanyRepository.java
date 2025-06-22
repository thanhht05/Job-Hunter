package vn.JobHunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.JobHunter.domain.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

}
