package vn.JobHunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
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
        if (!userOptional.isPresent()) {
            throw new EntityNotFoundException("User not found");
        }
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public Page<User> handleFetchAllUsers(Pageable pageable) {
        Page<User> users = this.userRepository.findAll(pageable);
        return users;
    }

    public User fetchUserByUsername(String username) {
        User user = this.userRepository.findByEmail(username);
        return user;
    }
}
