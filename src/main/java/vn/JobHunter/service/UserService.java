package vn.JobHunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.JobHunter.domain.Company;
import vn.JobHunter.domain.User;
import vn.JobHunter.domain.respone.ResultPaginationDto;
import vn.JobHunter.domain.respone.user.ResCreateUserDto;
import vn.JobHunter.domain.respone.user.ResUpdateUserDto;
import vn.JobHunter.domain.respone.user.ResponseUserDto;
import vn.JobHunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;

    public UserService(UserRepository userRepository, CompanyService companyService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
    }

    public User SaveUser(User user) {

        // check company
        Company c = user.getCompany();
        if (c != null) {
            Company company = this.companyService.fetchCompanyById(user.getCompany().getId());
            user.setCompany(company);
        }
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
            curUser.setCompany(user.getCompany());

            if (curUser.getCompany() != null) {
                Company c = this.companyService.fetchCompanyById(curUser.getCompany().getId());
                curUser.setCompany(c);
            }
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
                .stream().map(item -> new ResponseUserDto(
                        item.getId(),
                        item.getName(),
                        item.getEmail(),
                        item.getGender(),
                        item.getAddress(),
                        item.getAge(),
                        item.getCreatedDate(),
                        new ResponseUserDto.CompanyUser(
                                item.getCompany() != null ? item.getCompany().getId() : 0,
                                item.getCompany() != null ? item.getCompany().getName() : null))

                )

                .collect(Collectors.toList());
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

    public ResCreateUserDto convertUserToUserCreateDto(User user) {
        ResCreateUserDto res = new ResCreateUserDto();
        ResCreateUserDto.CompanyUser companyUser = new ResCreateUserDto.CompanyUser();

        res.setId(user.getId());
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setName(user.getName());
        res.setCreatedDate(user.getCreatedDate());

        if (user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            res.setCompany(companyUser);
        }
        return res;

    }

    public ResCreateUserDto convertUserToResUserDto(User user) {
        ResCreateUserDto resUserDto = new ResCreateUserDto();
        ResCreateUserDto.CompanyUser companyUser = new ResCreateUserDto.CompanyUser();

        resUserDto.setAddress(user.getAddress());
        resUserDto.setAge(user.getAge());
        resUserDto.setCreatedDate(user.getCreatedDate());
        resUserDto.setEmail(user.getEmail());
        resUserDto.setName(user.getName());
        resUserDto.setGender(user.getGender());
        resUserDto.setId(user.getId());
        if (user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
        }
        resUserDto.setCompany(companyUser);
        return resUserDto;

    }

    public ResUpdateUserDto convertUserToResUpdateUserDto(User user) {
        ResUpdateUserDto res = new ResUpdateUserDto();
        ResUpdateUserDto.CompanyUser companyUser = new ResUpdateUserDto.CompanyUser();
        res.setAddress(user.getAddress());
        res.setAge(user.getAge());
        res.setName(user.getName());
        res.setGender(user.getGender());
        res.setId(user.getId());
        res.setUpdatedAt(user.getUpdatedAt());

        if (user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
        }
        res.setCompany(companyUser);
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
