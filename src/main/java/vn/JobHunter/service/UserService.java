package vn.JobHunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import vn.JobHunter.domain.User;
import vn.JobHunter.domain.dto.Meta;
import vn.JobHunter.domain.dto.ResCreateUserDto;
import vn.JobHunter.domain.dto.ResUpdateUserDto;
import vn.JobHunter.domain.dto.ResponeUserDto;
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

    public User handleUpdateUser(User user) {
        User curUser = handleFetchUserById(user.getId());
        if (curUser != null) {
            curUser.setName(user.getName());
            curUser.setGender(user.getGender());
            curUser.setAddress(user.getAddress());
            curUser.setAge(user.getAge());

            curUser = this.userRepository.save(curUser);
        }
        return curUser;
    }

    public User handleFetchUserById(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
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

        // List<ResUserDto> resUserDtos = new ArrayList<>();
        // for (User user : users.getContent()) {
        // ResUserDto resUserDto = new ResUserDto(user.getId(), user.getName(),
        // user.getEmail(), user.getGender(),
        // user.getAddress(), user.getAge(), user.getCreatedAt(), user.getUpdatedAt());

        // resUserDtos.add(resUserDto);
        // }
        List<ResCreateUserDto> resUserDtos = users.getContent()
                .stream().map(item -> new ResCreateUserDto(

                        item.getId(),
                        item.getName(),
                        item.getEmail(),
                        item.getGender(),
                        item.getAddress(),
                        item.getAge(),
                        item.getCreatedDate(),
                        item.getUpdatedAt()))
                .collect(Collectors.toList());
        resultPaginationDto.setResult(resUserDtos);

        return resultPaginationDto;
    }

    public User fetchUserByUsername(String username) {
        User user = this.userRepository.findByEmail(username);
        return user;
    }

    public boolean checkExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public ResponeUserDto convertUserToUserCreateDto(User user) {
        ResponeUserDto responeUserDto = new ResponeUserDto();
        responeUserDto.setAddress(user.getAddress());
        responeUserDto.setAge(user.getAge());
        responeUserDto.setCreatedDate(user.getCreatedDate());
        responeUserDto.setEmail(user.getEmail());
        responeUserDto.setName(user.getName());
        responeUserDto.setGender(user.getGender());
        responeUserDto.setId(user.getId());
        return responeUserDto;
    }

    public ResCreateUserDto convertUserToResUserDto(User user) {
        ResCreateUserDto resUserDto = new ResCreateUserDto();
        resUserDto.setAddress(user.getAddress());
        resUserDto.setAge(user.getAge());
        resUserDto.setCreatedDate(user.getCreatedDate());
        resUserDto.setEmail(user.getEmail());
        resUserDto.setName(user.getName());
        resUserDto.setGender(user.getGender());
        resUserDto.setId(user.getId());
        resUserDto.setUpdatedAt(user.getUpdatedAt());
        return resUserDto;

    }

    public ResUpdateUserDto converUserToResUpdateUserDto(User user) {
        ResUpdateUserDto res = new ResUpdateUserDto();
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setName(user.getName());
        res.setGender(user.getGender());
        res.setId(user.getId());
        res.setUpdatedAt(user.getUpdatedAt());
        return res;
    }

    public void updateUserToken(String token, String email) {
        User user = this.fetchUserByUsername(email);
        if (user != null) {
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        User user = this.userRepository.findByRefreshTokenAndEmail(token, email);
        return user;
    }

}
