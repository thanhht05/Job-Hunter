package vn.JobHunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.JobHunter.domain.Permission;
import vn.JobHunter.domain.Role;
import vn.JobHunter.domain.respone.ResultPaginationDto;
import vn.JobHunter.repository.PermissionRepository;
import vn.JobHunter.util.exception.IdInvalidException;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission createPermission(Permission permission) throws IdInvalidException {
        if (this.permissionRepository.existsByModuleAndApiPathAndMethod(permission.getModule(), permission.getApiPath(),
                permission.getMethod())) {
            throw new IdInvalidException("Permission da ton tai");
        }

        return this.permissionRepository.save(permission);
    }

    public Permission fetchPermissionById(Long id) throws IdInvalidException {
        Optional<Permission> p = this.permissionRepository.findById(id);
        if (p.isEmpty()) {
            throw new IdInvalidException("Permission khong ton tai");
        }
        return p.get();
    }

    public Permission updatePermission(Permission permission) throws IdInvalidException {
        if (this.permissionRepository.existsByModuleAndApiPathAndMethod(permission.getModule(), permission.getApiPath(),
                permission.getMethod())) {
            throw new IdInvalidException("Permission da ton tai");
        }
        Permission p = this.fetchPermissionById(permission.getId());
        p.setApiPath(permission.getApiPath());
        p.setName(permission.getName());
        p.setMethod(permission.getMethod());
        return this.createPermission(permission);
    }

    public ResultPaginationDto getAllPermission(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> perPage = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDto res = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(perPage.getTotalPages());
        meta.setTotalElements(perPage.getTotalElements());

        res.setMeta(meta);
        res.setResult(perPage.getContent());
        return res;
    }

    public void deletePermission(Long id) throws IdInvalidException {
        Permission curPermission = this.fetchPermissionById(id);

        if (curPermission != null) {
            List<Role> roles = curPermission.getRoles();

            for (Role r : roles) {
                List<Permission> permissions = r.getPermissions();

                for (int i = 0; i < permissions.size(); i++) {
                    Permission p = permissions.get(i);
                    if (p != null && p.getId() == id) {

                        permissions.remove(i);
                        i--;
                    }
                }
            }
            this.permissionRepository.delete(curPermission);

        }
    }
}
