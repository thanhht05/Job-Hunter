package vn.JobHunter.domain.respone.resume;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.JobHunter.util.constant.StatusEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResResumeDto {
    private Long id;
    private String email;
    private String url;
    private StatusEnum status;
    private Instant createdDate;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
    private String companyName;
    private UserResume user;
    private JobResume job;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResume {
        private Long id;
        private String name;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobResume {
        private Long id;
        private String name;

    }
}