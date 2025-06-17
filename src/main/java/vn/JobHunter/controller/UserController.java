package vn.JobHunter.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import vn.JobHunter.domain.User;
import vn.JobHunter.service.UserService;
import vn.JobHunter.service.exception.IdInvalidException;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<User> handleCreateUser(@RequestBody User user) {
        User userCreate = this.userService.SaveUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userCreate);
    }

    @ExceptionHandler(IdInvalidException.class)
    public ResponseEntity<String> handleResourceNotFoundException(IdInvalidException invalidException) {
        return ResponseEntity.badRequest().body(invalidException.getMessage());
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
