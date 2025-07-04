package vn.JobHunter.domain.respone.resume;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateResumeDto {
    private Long id;
    private Instant createdDate;
    private String createdBy;

}
