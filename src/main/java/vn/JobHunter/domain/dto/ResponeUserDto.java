package vn.JobHunter.domain.dto;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.JobHunter.util.constant.GenderEnum;

@Getter
@Setter
public class ResponeUserDto {
    private Long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant createdDate;
}
