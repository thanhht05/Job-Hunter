package vn.JobHunter.domain.dto;

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
}
