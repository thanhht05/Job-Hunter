package vn.JobHunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.JobHunter.domain.Permission;
import vn.JobHunter.domain.respone.ResultPaginationDto;
import vn.JobHunter.service.PermissionService;
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

@RestController
@RequestMapping("/api/v1")
public class PermissionControler {
    private PermissionService permissionService;

    public PermissionControler(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> handleCreatePermission(@RequestBody Permission permission)
            throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.createPermission(permission));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update permission")
    public ResponseEntity<Permission> handleUpdatePermission(@RequestBody Permission p) throws IdInvalidException {
        return ResponseEntity.ok().body(this.permissionService.updatePermission(p));
    }

    @GetMapping("/permissions")
    @ApiMessage("Fetch all permissions")
    public ResponseEntity<ResultPaginationDto> handleGetAllPermission(@Filter Specification<Permission> spec,
            Pageable pageable) {

        return ResponseEntity.ok().body(this.permissionService.getAllPermission(spec, pageable));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> handleDeletePermission(@PathVariable("id") Long id) throws IdInvalidException {
        this.permissionService.deletePermission(id);
        return ResponseEntity.ok().body(null);
    }

}
