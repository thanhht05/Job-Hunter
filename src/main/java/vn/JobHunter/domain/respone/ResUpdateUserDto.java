package vn.JobHunter.domain.respone;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.JobHunter.util.constant.GenderEnum;

@Getter
@Setter
public class ResUpdateUserDto {
    private Long id;
    private GenderEnum gender;
    private String name;
    private String address;
    private int age;
    private Instant updatedAt;
    private CompanyUser company;

    @Getter
    @Setter
    public static class CompanyUser {
        private Long id;
        private String name;

    }
}
