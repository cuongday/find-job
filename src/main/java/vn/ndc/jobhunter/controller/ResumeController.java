package vn.ndc.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ndc.jobhunter.domain.Resume;
import vn.ndc.jobhunter.domain.response.ResultPaginationDTO;
import vn.ndc.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.ndc.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.ndc.jobhunter.domain.response.resume.ResUpdateDTO;
import vn.ndc.jobhunter.service.ResumeService;
import vn.ndc.jobhunter.util.annotation.ApiMessage;
import vn.ndc.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/resumes")
    @ApiMessage("Create a new resume")
    public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume resume) throws IdInvalidException {
        boolean isExist = this.resumeService.CheckResumeExitsByUserAndJob(resume);
        if(!isExist){
            throw new IdInvalidException("User hoặc Job không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.createResume(resume));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateDTO> updateResume(@RequestBody Resume resume) throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeService.fetchById(resume.getId());
        if(!resumeOptional.isPresent()){
            throw new IdInvalidException("Resume với id = " + resume.getId() + " không tồn tại");
        }
        Resume currentResume = resumeOptional.get();
        currentResume.setStatus(resume.getStatus());
        return ResponseEntity.status(HttpStatus.OK).body(this.resumeService.updateResume(currentResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume by id")
    public ResponseEntity<Void> deleteResume(@PathVariable long id) throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeService.fetchById(id);
        if(!resumeOptional.isPresent()){
            throw new IdInvalidException("Resume với id = " + id + " không tồn tại");
        }
        this.resumeService.deleteResume(resumeOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get a resume by id")
    public ResponseEntity<ResFetchResumeDTO> getResumeById(@PathVariable long id) throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeService.fetchById(id);
        if(!resumeOptional.isPresent()){
            throw new IdInvalidException("Resume với id = " + id + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.resumeService.getResume(resumeOptional.get()));
    }

    @GetMapping("/resumes")
    @ApiMessage("Get all resumes")
    public ResponseEntity<ResultPaginationDTO> getAllResumes(@Filter Specification<Resume> spec,
                                                             Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.resumeService.fetchAllResume(spec, pageable));
    }

}
