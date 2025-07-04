package vn.JobHunter.domain.respone.resume;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResUpdateResume {
    private Instant updatedDate;
    private String updatedBy;
}
