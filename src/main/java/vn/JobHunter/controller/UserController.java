package vn.JobHunter.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.JobHunter.domain.RestResponse;
import vn.JobHunter.domain.SearchCriteria;
import vn.JobHunter.domain.User;
import vn.JobHunter.domain.dto.ResCreateUserDto;
import vn.JobHunter.domain.dto.ResUpdateUserDto;
import vn.JobHunter.domain.dto.ResponeUserDto;
import vn.JobHunter.domain.dto.ResultPaginationDto;
import vn.JobHunter.service.UserService;
import vn.JobHunter.service.UserSpecification;
import vn.JobHunter.util.annotation.ApiMessage;
import vn.JobHunter.util.exception.IdInvalidException;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
@RequestMapping("/api/v1")
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
    @ApiMessage("Create a new user")
    public ResponseEntity<ResponeUserDto> handleCreateUser(@Valid @RequestBody User user)
            throws IdInvalidException {
        boolean checkEmail = userService.checkExistsByEmail(user.getEmail());
        if (checkEmail) {
            throw new IdInvalidException("Email " + user.getEmail() + " đã tồn tại trong hệ thống");
        }
        String hashPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User userCreate = this.userService.SaveUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertUserToUserCreateDto(userCreate));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> handleDeleteUser(@PathVariable("id") Long id) throws IdInvalidException {
        User user = this.userService.handleFetchUserById(id);
        if (user == null) {
            throw new IdInvalidException("User với id " + id + " không tồn tại trong hệ thống");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ResCreateUserDto> handleGetUserById(@PathVariable("id") Long id)
            throws IdInvalidException {
        User user = this.userService.handleFetchUserById(id);
        if (user == null) {
            throw new IdInvalidException("User với id " + id + " không tồn tại trong hệ thống");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertUserToResUserDto(user));
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDto> handleGetAllUsers(@Filter Specification<User> spec, Pageable pageable) {

        ResultPaginationDto users = this.userService.handleFetchAllUsers(pageable, spec);

        return ResponseEntity.ok(users);
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDto> handleUpdateUser(@RequestBody User user)
            throws IdInvalidException {
        User userUpdate = this.userService.handleUpdateUser(user);
        if (userUpdate == null) {
            throw new IdInvalidException("User  không tồn tại trong hệ thống");

        }
        this.userService.handleUpdateUser(userUpdate);

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.converUserToResUpdateUserDto(userUpdate));
    }

}
