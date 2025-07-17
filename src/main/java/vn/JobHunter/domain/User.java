package vn.JobHunter.domain;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.JobHunter.util.SecurityUtil;
import vn.JobHunter.util.constant.GenderEnum;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @NotBlank(message = "Email không được để trống")
    private String email;
    @NotBlank(message = "Password không được để trống")

    private String password;
    private int age;
    private String address;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;
    private String createdBy;
    private String updatedBy;
    private Instant createdDate;
    private Instant updatedAt;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Resume> resumes;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @PrePersist
    public void handleBeforeCreated() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        this.createdDate = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdated() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        this.updatedAt = Instant.now();
    }

}
