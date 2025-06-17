package vn.JobHunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.JobHunter.domain.User;
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
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public List<User> handleFetchAllUsers() {
        List<User> users = this.userRepository.findAll();
        return users;
    }
}
