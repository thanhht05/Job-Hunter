package vn.JobHunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.JobHunter.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
