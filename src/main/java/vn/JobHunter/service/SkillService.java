package vn.JobHunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.JobHunter.domain.Job;
import vn.JobHunter.domain.Skill;
import vn.JobHunter.domain.Subscriber;
import vn.JobHunter.domain.respone.ResultPaginationDto;
import vn.JobHunter.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill fetchSkillById(Long id) {
        Optional<Skill> curSkill = this.skillRepository.findById(id);
        return curSkill.isPresent() ? curSkill.get() : null;
    }

    public Skill createSkill(Skill skill) {
        Skill curSkill = this.skillRepository.save(skill);
        return curSkill;
    }

    public boolean checkSkillByName(String name) {
        return this.skillRepository.existsByName(name);
    }

    public ResultPaginationDto fetchAllSkill(Pageable pageable, Specification<Skill> spec) {
        Page<Skill> skillPage = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDto res = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(skillPage.getTotalPages());
        meta.setTotalElements(skillPage.getTotalElements());

        res.setMeta(meta);
        res.setResult(skillPage.getContent());
        return res;
    }

    public Skill handleUpdateSkill(Skill skill) {
        Skill curSkill = this.fetchSkillById(skill.getId());
        if (curSkill != null) {
            curSkill.setName(skill.getName());

            curSkill = this.createSkill(curSkill);
        }
        return curSkill;
    }

    public void deleteSkillById(Long id) {
        // tim skill theo id
        Skill skill = this.fetchSkillById(id);

        // neu co skill
        if (skill != null) {
            // lay skill cua job ra va xoa
            for (Job job : skill.getJobs()) {
                job.getSkills().remove(skill);
            }

            // for (Subscriber subs : skill.getSubscribers()) {
            // subs.getSkills().remove(skill);
            // }
            // xoa skill cua job xong thi xoa skill nay
            this.skillRepository.delete(skill);
        }
    }

}
