package vn.JobHunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.JobHunter.domain.Job;
import vn.JobHunter.domain.Skill;
import vn.JobHunter.domain.respone.ResultPaginationDto;
import vn.JobHunter.domain.respone.job.ResCreateJobDto;
import vn.JobHunter.domain.respone.job.ResUpdateJobDto;
import vn.JobHunter.service.JobService;
import vn.JobHunter.util.exception.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")

public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    public ResponseEntity<ResCreateJobDto> handleCreateJob(@RequestBody Job job) throws IdInvalidException {

        ResCreateJobDto curJob = this.jobService.createJob(job);
        return ResponseEntity.ok().body(curJob);
    }

    @PutMapping("/jobs")
    public ResponseEntity<ResUpdateJobDto> updateJob(@RequestBody Job job) {

        ResUpdateJobDto res = this.jobService.handleUpdateJob(job);
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> handelDeleteJob(@PathVariable("id") Long id) throws IdInvalidException {
        Job job = this.jobService.fetchJobById(id);
        if (job == null) {
            throw new IdInvalidException("Job khong ton tai");
        }
        this.jobService.deleteJob(job);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/jobs")
    public ResponseEntity<ResultPaginationDto> handleGetAllJob(@Filter Specification<Job> spec, Pageable pageable) {
        ResultPaginationDto res = this.jobService.fetchAllJob(spec, pageable);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> handleGetJobById(@PathVariable("id") Long id) throws IdInvalidException {
        Job job = this.jobService.fetchJobById(id);
        if (job == null) {
            throw new IdInvalidException("Job khong ton tai");

        }
        return ResponseEntity.ok().body(job);
    }

}
