package vn.JobHunter.domain;

import java.time.Instant;

import org.hibernate.validator.constraints.ru.INN;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "companies")
@Getter
@Setter
public class Comapy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private String logo;
    private String address;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant updatedBy;
    private Instant createdBy;

}
