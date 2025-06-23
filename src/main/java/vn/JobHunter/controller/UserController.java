package vn.JobHunter.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.turkraft.springfilter.boot.Filter;

import vn.JobHunter.domain.RestResponse;
import vn.JobHunter.domain.SearchCriteria;
import vn.JobHunter.domain.User;
import vn.JobHunter.domain.dto.ResultPaginationDto;
import vn.JobHunter.service.UserService;
import vn.JobHunter.service.UserSpecification;
import vn.JobHunter.util.annotation.ApiMessage;
import vn.JobHunter.util.exception.IdInvalidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public ResponseEntity<Object> hello() {
        RestResponse<Object> res = new RestResponse<>();
        res.setMessage("hiihihi");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/users")
    public ResponseEntity<User> handleCreateUser(@RequestBody User user) {

        String hashPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User userCreate = this.userService.SaveUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userCreate);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> handleDeleteUser(@PathVariable("id") Long id) throws IdInvalidException {
        if (id > 100) {
            throw new IdInvalidException("loi ");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> handleGetUserById(@PathVariable("id") Long id) throws UsernameNotFoundException {
        User user = this.userService.handleFetchUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDto> handleGetAllUsers(@Filter Specification<User> spec, Pageable pageable) {

        ResultPaginationDto users = this.userService.handleFetchAllUsers(pageable, spec);

        return ResponseEntity.ok(users);
    }

    @PutMapping("users/{id}")
    public ResponseEntity<User> putMethodName(@PathVariable("id") Long id, @RequestBody User user) {
        User userUpdate = this.userService.handleFetchUserById(id);
        if (userUpdate != null) {
            userUpdate.setEmail(user.getEmail());
            userUpdate.setFullName(user.getFullName());
            userUpdate.setPassword(user.getPassword());

            userUpdate = this.userService.SaveUser(userUpdate);
        }

        return ResponseEntity.ok(user);
    }

}
