package vn.ndc.jobhunter.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.ndc.jobhunter.domain.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long>{
    boolean existsByName(String name);
    Page findAll(Specification<Skill> spec, Pageable pageable);
}
