package vn.JobHunter.domain.respone.role;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateRoleDto {
    private Long id;
    private boolean active;
    private Instant createdDate;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
    private String name;
    private String description;
    private List<String> permissions;

    // @Getter
    // @Setter
    // public static class PermissionRole {
    // private Long id;
    // private String name;
    // }
}
