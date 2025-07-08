package vn.JobHunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.JobHunter.domain.Permission;
import vn.JobHunter.domain.Role;
import vn.JobHunter.domain.respone.ResultPaginationDto;
import vn.JobHunter.domain.respone.role.ResCreateRoleDto;
import vn.JobHunter.repository.PermissionRepository;
import vn.JobHunter.repository.RoleRepository;
import vn.JobHunter.util.exception.IdInvalidException;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role fetchRoleById(Long id) throws IdInvalidException {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if (roleOptional.isEmpty()) {
            throw new IdInvalidException("Role khong ton tai");
        }
        return roleOptional.get();
    }

    public ResCreateRoleDto createRole(Role role) throws IdInvalidException {
        if (this.roleRepository.existsByName(role.getName())) {
            throw new IdInvalidException("Role da ton tai");
        }

        if (role.getPermissions() != null) {
            List<Long> reqPermissions = role.getPermissions().stream()
                    .map(i -> i.getId())
                    .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            role.setPermissions(dbPermissions);
        }
        Role curRole = this.roleRepository.save(role);

        ResCreateRoleDto res = new ResCreateRoleDto();

        res.setActive(role.isActive());
        res.setCreatedBy(role.getCreatedBy());
        res.setCreatedDate(role.getCreatedDate());
        res.setUpdatedBy(role.getUpdatedBy());
        res.setUpdatedAt(role.getUpdatedAt());
        res.setName(role.getName());
        res.setId(role.getId());
        res.setDescription(role.getDescription());

        if (curRole.getPermissions() != null) {
            List<String> permissionsRole = curRole.getPermissions().stream().map(x -> x.getName())
                    .collect(Collectors.toList());
            res.setPermissions(permissionsRole);
        }
        return res;

    }

    public Role updateRole(Role role) throws IdInvalidException {
        Optional<Role> roleOptional = this.roleRepository.findById(role.getId());
        if (roleOptional.isEmpty()) {
            throw new IdInvalidException("Role khong ton tai");
        }

        Role cuRole = roleOptional.get();
        cuRole.setActive(role.isActive());
        cuRole.setName(role.getName());
        cuRole.setDescription(role.getDescription());
        cuRole.setPermissions(role.getPermissions());

        return this.roleRepository.save(cuRole);

    }

    public ResultPaginationDto getAllRole(Specification<Role> spec, Pageable pageable) {
        Page<Role> rolePage = this.roleRepository.findAll(spec, pageable);

        ResultPaginationDto res = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalElements(rolePage.getTotalElements());
        meta.setPages(rolePage.getTotalPages());
        res.setMeta(meta);

        res.setResult(rolePage.getContent());

        return res;
    }

    public void deleteRole(Long id) throws IdInvalidException {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if (roleOptional.isEmpty()) {
            throw new IdInvalidException("Role khong ton tai");
        }
        this.roleRepository.delete(roleOptional.get());

    }

}
