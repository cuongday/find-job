package vn.ndc.jobhunter.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.ndc.jobhunter.domain.Skill;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long>, JpaSpecificationExecutor<Skill> {
    boolean existsByName(String name);
    List<Skill> findByIdIn(List<Long> id);
    Page findAll(Specification<Skill> spec, Pageable pageable);
}
