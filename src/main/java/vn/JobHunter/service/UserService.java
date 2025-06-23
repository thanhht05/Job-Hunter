package vn.JobHunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import vn.JobHunter.domain.User;
import vn.JobHunter.domain.dto.Meta;
import vn.JobHunter.domain.dto.ResultPaginationDto;
import vn.JobHunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User SaveUser(User user) {
        User userCreate = this.userRepository.save(user);
        return userCreate;
    }

    public void handleDeleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    public User handleFetchUserById(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (!userOptional.isPresent()) {
            throw new EntityNotFoundException("User not found");
        }
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public ResultPaginationDto handleFetchAllUsers(Pageable pageable, Specification<User> spec) {
        Page<User> users = this.userRepository.findAll(spec, pageable);
        ResultPaginationDto resultPaginationDto = new ResultPaginationDto();
        Meta meta = new Meta();

        meta.setPage(pageable.getPageNumber() + 1);// get current page number
        meta.setPageSize(pageable.getPageSize()); // get max-total elements
        meta.setPages(users.getTotalPages()); // get totla pages
        meta.setTotalElements(users.getTotalElements()); // get total elements in database

        resultPaginationDto.setMeta(meta);
        resultPaginationDto.setResult(users.getContent());
        return resultPaginationDto;
    }

    public User fetchUserByUsername(String username) {
        User user = this.userRepository.findByEmail(username);
        return user;
    }
}
