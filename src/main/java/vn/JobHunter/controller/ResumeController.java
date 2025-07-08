package vn.JobHunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.JobHunter.domain.Resume;
import vn.JobHunter.domain.respone.ResultPaginationDto;
import vn.JobHunter.domain.respone.resume.ResCreateResumeDto;
import vn.JobHunter.domain.respone.resume.ResResumeDto;
import vn.JobHunter.domain.respone.resume.ResUpdateResume;
import vn.JobHunter.service.ResumeService;
import vn.JobHunter.util.annotation.ApiMessage;
import vn.JobHunter.util.exception.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create resume")
    public ResponseEntity<ResCreateResumeDto> handleCreateResume(@Valid @RequestBody Resume resume)
            throws IdInvalidException {

        ResCreateResumeDto res = this.resumeService.createResume(resume);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("resumes")
    @ApiMessage("Update resume")
    public ResponseEntity<ResUpdateResume> handleUpdateResume(@RequestBody Resume resume) throws IdInvalidException {

        ResUpdateResume res = this.resumeService.updateResume(resume);

        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete resume")
    public ResponseEntity<Void> handleDeleteResume(@PathVariable("id") Long id) throws IdInvalidException {
        this.resumeService.handleDeleteResume(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Fetch resume by id")
    public ResponseEntity<ResResumeDto> handleFetchResume(@PathVariable("id") Long id) throws IdInvalidException {

        ResResumeDto res = this.resumeService.fetchResumeById(id);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/resumes")
    @ApiMessage("Fetch all resume")
    public ResponseEntity<ResultPaginationDto> fetchAllResume(@Filter Specification<Resume> spec, Pageable pageable)
            throws IdInvalidException {
        return ResponseEntity.ok().body(this.resumeService.fetchAllResume(spec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Fetch all resume by user")
    public ResponseEntity<ResultPaginationDto> fetchResumeByUser(Pageable pageable) {

        return ResponseEntity.ok().body(this.resumeService.getResumeByUser(pageable));
    }

}
