package vn.JobHunter.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import vn.JobHunter.domain.RestResponse;
import vn.JobHunter.domain.User;
import vn.JobHunter.service.UserService;
import vn.JobHunter.util.exception.IdInvalidException;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<User> getMethodName() {
        Long id = (long) 9;
        User user = this.userService.handleFetchUserById(id);
        RestResponse<String> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Welcome!");
        response.setData("This is public data");
        return ResponseEntity.ok().body(user);
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
    public ResponseEntity<User> handleGetUserByid(@PathVariable("id") Long id) {
        User user = this.userService.handleFetchUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> handleGetAllUsers() {
        List<User> users = this.userService.handleFetchAllUsers();
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
