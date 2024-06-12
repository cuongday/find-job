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
import vn.ndc.jobhunter.domain.Job;
import vn.ndc.jobhunter.domain.response.ResultPaginationDTO;
import vn.ndc.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.ndc.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.ndc.jobhunter.service.JobService;
import vn.ndc.jobhunter.util.annotation.ApiMessage;
import vn.ndc.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @PostMapping("/jobs")
    @ApiMessage("Create a job")
    public ResponseEntity<ResCreateJobDTO> create(@Valid @RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.jobService.create(job));
    }

    @PutMapping("/jobs")
    @ApiMessage("Update a job")
    public ResponseEntity<ResUpdateJobDTO> update(@Valid @RequestBody Job job) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.fetchJobById(job.getId());
        if(!currentJob.isPresent()) {
            throw new IdInvalidException( "Job not found");
        }

        return ResponseEntity.ok()
                .body(this.jobService.update(job));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job by id")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.fetchJobById(id);
        if(!currentJob.isPresent()) {
            throw new IdInvalidException( "Job not found");
        }

        this.jobService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Get a job by id")
    public ResponseEntity<Job> getJobById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.fetchJobById(id);
        if(!currentJob.isPresent()) {
            throw new IdInvalidException( "Job not found");
        }

        return ResponseEntity.ok().body(currentJob.get());
    }

    @GetMapping("/jobs")
    @ApiMessage("Get jobs with pagiantion")
    public ResponseEntity<ResultPaginationDTO> getJobs(@Filter Specification<Job> spec,
                                                        Pageable pageable) {
        return ResponseEntity.ok().body(this.jobService.fetchAll(spec, pageable));
    }
}
