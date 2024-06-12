package vn.ndc.jobhunter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ndc.jobhunter.domain.Skill;
import vn.ndc.jobhunter.domain.response.ResultPaginationDTO;
import vn.ndc.jobhunter.repository.SkillRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    public boolean existsByName(String name) {
        return skillRepository.existsByName(name);
    }

    public Skill handleCreateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public Skill fetchSkillById(Long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        if (skillOptional.isPresent()) {
            return skillOptional.get();
        }
        return null;
    }

    public Skill handleUpdateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public ResultPaginationDTO handleGetSkill(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageSkill.getTotalPages());
        meta.setTotal(pageSkill.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageSkill.getContent());
        return rs;
    }

    public void handleDeleteSkill(long id){
        //delete job
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        Skill currentSkill = skillOptional.get();
        currentSkill.getJobs().forEach((job) -> job.getSkills().remove(currentSkill));

        this.skillRepository.delete(currentSkill);
    }
}
