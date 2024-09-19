package vn.ndc.jobhunter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ndc.jobhunter.domain.Permission;
import vn.ndc.jobhunter.domain.response.ResultPaginationDTO;
import vn.ndc.jobhunter.repository.PermissionRepository;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public boolean isPermissionExist(Permission permission) {
        return permissionRepository.existsByModuleAndApiPathAndMethod(
                permission.getModule(),
                permission.getApiPath(),
                permission.getMethod()
        );
    }

    public Permission handleCreatePermission(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Permission fetchById(long id){
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        if(permissionOptional.isPresent()){
            return permissionOptional.get();
        }
        return null;
    }

    public Permission handleUpdatePermission(Permission permission){
        Permission permissionDB = this.fetchById(permission.getId());
        if(permissionDB != null){
            permissionDB.setModule(permission.getModule());
            permissionDB.setApiPath(permission.getApiPath());
            permissionDB.setMethod(permission.getMethod());
            permissionDB.setName(permission.getName());

            permissionDB = this.permissionRepository.save(permissionDB);
            return permissionDB;
        }
        return null;
    }

    public ResultPaginationDTO handleGetPermission(Specification<Permission> permissionSpec, Pageable pageable){
        Page<Permission> pagePermission = this.permissionRepository.findAll(permissionSpec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pagePermission.getTotalPages());
        meta.setTotal(pagePermission.getTotalElements());

        rs.setMeta(meta);

        rs.setResult(pagePermission.getContent());

        return rs;
    }

    public void handleDeletePermission(long id){
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        Permission currentPermission = permissionOptional.get();
        currentPermission.getRoles().forEach((role) -> role.getPermissions().remove(currentPermission));
        this.permissionRepository.delete(currentPermission);
    }

    public boolean isSameName(Permission permission) {
        Permission permissionDB = this.fetchById(permission.getId());
        if(permissionDB != null){
            if(permissionDB.getName().equals(permission.getName())){
                return true;
            }
        }
        return false;
    }
}
