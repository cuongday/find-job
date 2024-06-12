package vn.ndc.jobhunter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.ndc.jobhunter.util.SecurityUtil;
import vn.ndc.jobhunter.util.constant.LevelEnum;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "jobs")
@Setter
@Getter
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "name không được để trống")
    private String name;

    @NotBlank(message = "location không được để trống")
    private String location;
    private double salary;
    private int quantity;
    private LevelEnum level;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private Instant startDate;
    private Instant endDate;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private String updatedBy;
    private String createdBy;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"jobs"})
    @JoinTable(name = "job_skills",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills;


    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
    }
}
