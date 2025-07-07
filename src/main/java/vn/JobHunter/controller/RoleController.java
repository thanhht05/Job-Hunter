package vn.JobHunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.JobHunter.domain.Role;
import vn.JobHunter.domain.respone.ResultPaginationDto;
import vn.JobHunter.domain.respone.job.ResCreateJobDto;
import vn.JobHunter.domain.respone.role.ResCreateRoleDto;
import vn.JobHunter.service.RoleService;
import vn.JobHunter.util.annotation.ApiMessage;
import vn.JobHunter.util.exception.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<ResCreateRoleDto> handleCreateRole(@RequestBody Role role) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.createRole(role));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> handleUpdateRole(@RequestBody Role role) throws IdInvalidException {

        return ResponseEntity.ok().body(this.roleService.updateRole(role));
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch all roles")
    public ResponseEntity<ResultPaginationDto> handleGetAllRoles(@Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.roleService.getAllRole(spec, pageable));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> handleDeleteRole(@PathVariable("id") Long id) throws IdInvalidException {
        this.roleService.deleteRole(id);
        return ResponseEntity.ok().body(null);
    }

}
