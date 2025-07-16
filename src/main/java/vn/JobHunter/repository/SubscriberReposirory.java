package vn.JobHunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.JobHunter.domain.Role;
import vn.JobHunter.domain.Subscriber;

@Repository
public interface SubscriberReposirory extends JpaRepository<Subscriber, Long>, JpaSpecificationExecutor<Role> {
    boolean existsByEmail(String email);
}
