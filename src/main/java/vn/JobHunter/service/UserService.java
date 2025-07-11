package vn.JobHunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.JobHunter.domain.Company;
import vn.JobHunter.domain.Role;
import vn.JobHunter.domain.User;
import vn.JobHunter.domain.respone.ResultPaginationDto;
import vn.JobHunter.domain.respone.user.ResponseUserDto;
import vn.JobHunter.repository.UserRepository;
import vn.JobHunter.util.exception.IdInvalidException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompanyService companyService, RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public ResponseUserDto createUser(User user) throws IdInvalidException {
        // check role
        if (user.getRole() != null) {
            Role r = this.roleService.fetchRoleById(user.getRole().getId());
            user.setRole(r != null ? r : null);
        }

        // check company
        Company c = user.getCompany();
        if (c != null) {
            Company company = this.companyService.fetchCompanyById(user.getCompany().getId());
            user.setCompany(company);
        }
        User userCreate = this.userRepository.save(user);

        return this.convertUserToResUserDto(userCreate);
    }

    public void handleDeleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    public User handleUpdateUser(User user) throws IdInvalidException {
        User curUser = handleFetchUserById(user.getId());
        if (curUser != null) {
            curUser.setName(user.getName());
            curUser.setGender(user.getGender());
            curUser.setAddress(user.getAddress());
            curUser.setAge(user.getAge());
            curUser.setCompany(user.getCompany());

            if (user.getCompany() != null && user.getCompany().getId() != null) {
                Company c = companyService.fetchCompanyById(user.getCompany().getId());
                curUser.setCompany(c);
            } else {
                curUser.setCompany(curUser.getCompany()); // giữ nguyên
            }

            if (user.getRole() != null && user.getRole().getId() != null) {
                Role role = this.roleService.fetchRoleById(user.getRole().getId());
                curUser.setRole(role != null ? role : null);
            } else {

                curUser = this.userRepository.save(curUser);
            }
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
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();

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
        List<ResponseUserDto> res = users.getContent()
                .stream().map(item -> this.convertUserToResUserDto(item)).collect(Collectors.toList());
        resultPaginationDto.setResult(res);

        return resultPaginationDto;
    }

    public User fetchUserByUsername(String username) {
        User user = this.userRepository.findByEmail(username);
        return user;
    }

    public boolean checkExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public ResponseUserDto convertUserToResUserDto(User user) {
        ResponseUserDto res = new ResponseUserDto();
        ResponseUserDto.CompanyUser companyUser = new ResponseUserDto.CompanyUser();
        ResponseUserDto.RoleUser role = new ResponseUserDto.RoleUser();

        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setCreatedDate(user.getCreatedDate());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setGender(user.getGender());
        res.setId(user.getId());
        if (user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
        }
        res.setCompany(companyUser);

        if (user.getRole() != null) {
            role.setId(user.getRole().getId());
            role.setName(user.getRole().getName());
        }
        res.setRole(role);
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
