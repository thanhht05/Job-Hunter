package vn.JobHunter.domain.dto;

import lombok.Getter;
import lombok.Setter;
import vn.JobHunter.domain.Company;
import vn.JobHunter.util.constant.GenderEnum;

@Getter
@Setter
public class UserDto {
    private String email;
    private String name;
    private String password;
    private String address;
    private GenderEnum gender;
    private int age;
    private CompanyDto company;

    @Getter
    @Setter
    public static class CompanyDto {

        private Long id;
    }
}
