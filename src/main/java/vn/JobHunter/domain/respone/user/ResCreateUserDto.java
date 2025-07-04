package vn.JobHunter.domain.respone.user;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.JobHunter.util.constant.GenderEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResCreateUserDto {
    private Long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant createdDate;
    private CompanyUser company;

    @Getter
    @Setter
    public static class CompanyUser {
        private Long id;
        private String name;

    }

}
