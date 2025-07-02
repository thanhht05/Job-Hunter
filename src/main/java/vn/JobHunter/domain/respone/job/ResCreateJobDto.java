package vn.JobHunter.domain.respone.job;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import vn.JobHunter.domain.Job;
import vn.JobHunter.domain.Skill;
import vn.JobHunter.util.constant.LevelEnum;

@Getter
@Setter
public class ResCreateJobDto {
    private Long id;
    private String name;
    private String location;
    private int quantity;
    private Double salary;
    private LevelEnum level;
    private Instant startDate;
    private Instant endDate;
    private Boolean isActive;
    private List<String> skills;
    private Instant createDate;
    private String createdBy;

}
