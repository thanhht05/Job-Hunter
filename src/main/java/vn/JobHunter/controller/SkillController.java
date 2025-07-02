package vn.JobHunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.JobHunter.domain.Skill;
import vn.JobHunter.domain.respone.ResultPaginationDto;
import vn.JobHunter.service.SkillService;
import vn.JobHunter.util.annotation.ApiMessage;
import vn.JobHunter.util.exception.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")

public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/skills")
    @ApiMessage("Fetch all skill")
    public ResponseEntity<ResultPaginationDto> handleGetAllSkill(@Filter Specification<Skill> spec, Pageable pageable) {
        ResultPaginationDto skills = this.skillService.fetchAllSkill(pageable, spec);
        return ResponseEntity.ok(skills);

    }

    @PostMapping("/skills")

    public ResponseEntity<Skill> handleCreateSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {

        Boolean checkExistsName = this.skillService.checkSkillByName(skill.getName());
        if (checkExistsName) {
            throw new IdInvalidException("Skill da ton tai");
        }
        Skill curSkill = this.skillService.createSkill(skill);

        return ResponseEntity.ok().body(curSkill);
    }

    @PutMapping("skills")
    public ResponseEntity<Skill> handleUpdateSkill(@Valid @RequestBody Skill skill) {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.handleUpdateSkill(skill));
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") Long id) throws IdInvalidException {
        Skill skill = this.skillService.fetchSkillById(id);
        if (skill == null) {
            throw new IdInvalidException("Skill khong ton tai");
        }
        this.skillService.deleteSkillById(id);
        return ResponseEntity.ok().body(null);

    }

}
