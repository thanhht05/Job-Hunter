package vn.JobHunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import vn.JobHunter.domain.Job;
import vn.JobHunter.domain.Resume;
import vn.JobHunter.domain.User;
import vn.JobHunter.domain.respone.ResultPaginationDto;
import vn.JobHunter.domain.respone.resume.ResCreateResumeDto;
import vn.JobHunter.domain.respone.resume.ResResumeDto;
import vn.JobHunter.domain.respone.resume.ResUpdateResume;
import vn.JobHunter.repository.ResumeRepository;
import vn.JobHunter.util.SecurityUtil;
import vn.JobHunter.util.exception.IdInvalidException;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final JobService jobService;
    private final UserService userService;
    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    public ResumeService(ResumeRepository resumeRepository, JobService jobService, UserService userService) {
        this.resumeRepository = resumeRepository;
        this.jobService = jobService;
        this.userService = userService;
    }

    public ResCreateResumeDto createResume(Resume resume) throws IdInvalidException {
        Job j = this.jobService.fetchJobById(resume.getJob().getId());
        User user = this.userService.handleFetchUserById(resume.getUser().getId());
        if (user == null) {
            throw new IdInvalidException("user/Job khong ton tai");

        }
        if (j == null) {
            throw new IdInvalidException("user/Job khong ton tai");

        }

        if (resume.getUser() == null) {
        }
        this.resumeRepository.save(resume);

        ResCreateResumeDto res = new ResCreateResumeDto();
        res.setCreatedBy(resume.getCreatedBy());
        res.setId(resume.getId());
        res.setCreatedDate(resume.getCreatedDate());
        return res;
    }

    public ResUpdateResume updateResume(Resume resume) throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeRepository.findById(resume.getId());
        Resume curResumere = resumeOptional.orElseThrow(() -> new IdInvalidException("Resume is not found"));
        curResumere.setStatus(resume.getStatus());
        this.createResume(curResumere);

        ResUpdateResume res = new ResUpdateResume();
        res.setUpdatedDate(curResumere.getCreatedDate());
        res.setUpdatedBy(curResumere.getUpdatedBy());
        return res;

    }

    public void handleDeleteResume(Long id) throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeRepository.findById(id);
        Resume curResumere = resumeOptional.orElseThrow(() -> new IdInvalidException("Resume is not found"));

        this.resumeRepository.delete(curResumere);
    }

    public ResResumeDto fetchResumeById(Long id) throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeRepository.findById(id);
        Resume curResumere = resumeOptional.orElseThrow(() -> new IdInvalidException("Resume is not found"));
        return convertResumeToResResumeDto(curResumere);

    }

    public ResResumeDto convertResumeToResResumeDto(Resume resume) {
        ResResumeDto res = new ResResumeDto();
        ResResumeDto.JobResume job = new ResResumeDto.JobResume();
        ResResumeDto.UserResume user = new ResResumeDto.UserResume();

        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setCreatedBy(resume.getCreatedBy());
        res.setCreatedDate(resume.getCreatedDate());
        res.setStatus(resume.getStatus());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());

        res.setCompanyName(resume.getJob().getCompany() != null ? resume.getJob().getCompany().getName() : null);

        job.setId(resume.getJob().getId());
        job.setName(resume.getJob().getName());

        user.setId(resume.getUser().getId());
        user.setName(resume.getUser().getName());

        res.setJob(job);
        res.setUser(user);

        return res;
    }

    public ResultPaginationDto fetchAllResume(Specification<Resume> spec, Pageable pageable) throws IdInvalidException {
        Page<Resume> resumePage = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDto res = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalElements(resumePage.getTotalElements());
        meta.setPages(resumePage.getTotalPages());

        res.setMeta(meta);
        List<ResResumeDto> resResumeDtos = new ArrayList<>();
        for (Resume r : resumePage.getContent()) {
            ResResumeDto resResumeDto = this.fetchResumeById(r.getId());
            resResumeDtos.add(resResumeDto);
        }
        res.setResult(resResumeDtos);

        return res;
    }

    public ResultPaginationDto getResumeByUser(Pageable pageable) {
        // query builder
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        FilterNode node = filterParser.parse("user.email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
        Page<Resume> resumePage = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDto res = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setTotalElements(resumePage.getTotalElements());
        meta.setPages(resumePage.getTotalPages());

        res.setMeta(meta);

        List<ResResumeDto> resumeDtoList = resumePage.getContent().stream()
                .map(r -> this.convertResumeToResResumeDto(r))
                .collect(Collectors.toList());

        res.setResult(resumeDtoList);
        return res;
    }
}
