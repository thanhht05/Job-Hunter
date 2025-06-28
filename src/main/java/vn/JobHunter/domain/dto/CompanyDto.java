package vn.JobHunter.domain.dto;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDto {
    private Long id;
    private String name;
    private String address;
    private Instant createdDate;
    private String createdBy;
    private String description;
}
