package vn.JobHunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.JobHunter.domain.Company;
import vn.JobHunter.domain.Job;
import vn.JobHunter.domain.Skill;
import vn.JobHunter.domain.respone.ResultPaginationDto;
import vn.JobHunter.domain.respone.job.ResCreateJobDto;
import vn.JobHunter.domain.respone.job.ResUpdateJobDto;
import vn.JobHunter.repository.JobRepository;
import vn.JobHunter.repository.SkillRepository;
import vn.JobHunter.util.exception.IdInvalidException;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyService companyService;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository, CompanyService companyService) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyService = companyService;
    }

    public ResCreateJobDto createJob(Job job) throws IdInvalidException {
        Company c = this.companyService.fetchCompanyById(job.getCompany().getId());
        if (c == null) {
            throw new IdInvalidException("Company not found");
        }
        // check skill
        // if (job.getSkills() != null) {
        // List<Long> reqSkills = job.getSkills()
        // .stream().map(x -> x.getId())
        // .collect(Collectors.toList());

        // List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
        // job.setSkills(dbSkills);
        // }
        // job.setCompany(c);

        // or
        if (job.getSkills() != null) {
            List<Long> reqSkill = new ArrayList<>();
            for (Skill s : job.getSkills()) {
                reqSkill.add(s.getId());
            }

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkill);
            job.setSkills(dbSkills);
        }
        // create job
        Job curJob = this.jobRepository.save(job);

        // convert to dto
        ResCreateJobDto res = new ResCreateJobDto();
        res.setId(job.getId());
        res.setLevel(job.getLevel());
        res.setCreatedBy(job.getCreatedBy());
        res.setLocation(job.getLocation());
        res.setCreateDate(job.getCreatedDate());
        res.setIsActive(job.isActive());
        res.setSalary(job.getSalary());
        res.setName(job.getName());

        if (curJob.getSkills() != null) {
            List<String> skillList = curJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            res.setSkills(skillList);
        }
        return res;
    }

    public ResUpdateJobDto handleUpdateJob(Job j) throws IdInvalidException {
        Job existingJob = jobRepository.findById(j.getId())
                .orElseThrow(() -> new IdInvalidException("Job not found"));

        j.setCreatedBy(existingJob.getCreatedBy());
        j.setCreatedDate(existingJob.getCreatedDate());

        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills()
                    .stream().map(Skill::getId)
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            j.setSkills(dbSkills);
        }

        Job curJob = this.jobRepository.save(j);

        ResUpdateJobDto res = new ResUpdateJobDto();
        res.setId(curJob.getId());
        res.setName(curJob.getName());
        res.setLevel(curJob.getLevel());
        res.setIsActive(curJob.isActive());
        res.setQuantity(curJob.getQuantity());
        res.setSalary(curJob.getSalary());
        res.setLocation(curJob.getLocation());
        res.setStartDate(curJob.getStartDate());
        res.setEndDate(curJob.getEndDate());

        if (curJob.getSkills() != null) {
            List<String> skillList = curJob.getSkills()
                    .stream().map(Skill::getName)
                    .collect(Collectors.toList());
            res.setSkills(skillList);
        }

        return res;
    }

    public Job fetchJobById(Long id) {
        Optional<Job> jop = this.jobRepository.findById(id);
        return jop.isPresent() ? jop.get() : null;
    }

    public void deleteJob(Job j) {
        this.jobRepository.delete(j);
    }

    public ResultPaginationDto fetchAllJob(Specification<Job> spec, Pageable pageable) {
        Page<Job> jobPage = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDto res = new ResultPaginationDto();

        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalElements(jobPage.getTotalElements());
        meta.setPages(jobPage.getTotalPages());

        res.setMeta(meta);
        res.setResult(jobPage.getContent());
        return res;
    }
}
