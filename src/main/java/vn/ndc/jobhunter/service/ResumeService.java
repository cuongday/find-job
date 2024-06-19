package vn.ndc.jobhunter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ndc.jobhunter.domain.Job;
import vn.ndc.jobhunter.domain.Resume;
import vn.ndc.jobhunter.domain.User;
import vn.ndc.jobhunter.domain.response.ResultPaginationDTO;
import vn.ndc.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.ndc.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.ndc.jobhunter.domain.response.resume.ResUpdateDTO;
import vn.ndc.jobhunter.repository.JobRepository;
import vn.ndc.jobhunter.repository.ResumeRepository;
import vn.ndc.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ResCreateResumeDTO createResume(Resume resume){
        this.resumeRepository.save(resume);
        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setId(resume.getId());
        return res;
    }

    public ResUpdateDTO updateResume(Resume resume){
        resume = this.resumeRepository.save(resume);
        ResUpdateDTO res = new ResUpdateDTO();
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        return res;
    }

    public boolean CheckResumeExitsByUserAndJob(Resume resume){
        if(resume.getUser() == null || resume.getJob() == null){
            return false;
        }

        Optional<User> userOptional = this.userRepository.findById(resume.getUser().getId());
        Optional<Job> jobOptional = this.jobRepository.findById(resume.getJob().getId());
        if(!userOptional.isPresent() || !jobOptional.isPresent()){
            return false;
        }
        return true;
    }

    public Optional<Resume> fetchById(long id){
        return this.resumeRepository.findById(id);
    }

    public void deleteResume(Resume resume){
        this.resumeRepository.delete(resume);
    }

    public ResFetchResumeDTO getResume(Resume resume){
        ResFetchResumeDTO res = new ResFetchResumeDTO();
        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setStatus(resume.getStatus());
        res.setCreatedAt(resume.getCreatedAt());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setUpdatedBy(resume.getUpdatedBy());
        if(resume.getJob() != null){
            res.setCompanyName(resume.getJob().getCompany().getName());
        }
        res.setUser(new ResFetchResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));
        res.setJob(new ResFetchResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName()));
        return res;
    }

    public ResultPaginationDTO fetchAllResume(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        rs.setMeta(mt);

        rs.setResult(pageResume.getContent());

        List<ResFetchResumeDTO> listResume = pageResume.getContent()
                .stream().map(item -> this.getResume(item))
                .collect(Collectors.toList());
        rs.setResult(listResume);

        return rs;
    }

}
