package vn.ndc.jobhunter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.ndc.jobhunter.domain.Job;
import vn.ndc.jobhunter.domain.Resume;
import vn.ndc.jobhunter.domain.User;
import vn.ndc.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.ndc.jobhunter.repository.JobRepository;
import vn.ndc.jobhunter.repository.ResumeRepository;
import vn.ndc.jobhunter.repository.UserRepository;

import java.util.Optional;

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


}
