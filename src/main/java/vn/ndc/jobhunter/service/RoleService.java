package vn.ndc.jobhunter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ndc.jobhunter.domain.Job;
import vn.ndc.jobhunter.domain.Permission;
import vn.ndc.jobhunter.domain.Role;
import vn.ndc.jobhunter.domain.response.ResultPaginationDTO;
import vn.ndc.jobhunter.repository.PermissionRepository;
import vn.ndc.jobhunter.repository.RoleRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public Role createRole(Role role) {
        if(role.getPermissions() != null){
            List<Long> reqPermissions = role.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            role.setPermissions(dbPermissions);
        }
        return this.roleRepository.save(role);
    }

    public boolean existsByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role updateRole(Role role){
        Role roleDB = this.fetchRoleById(role.getId());
        if(role.getPermissions() != null){
            List<Long> reqPermissions = role.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            role.setPermissions(dbPermissions);
        }
        roleDB.setName(role.getName());
        roleDB.setPermissions(role.getPermissions());
        roleDB.setActive(role.isActive());
        roleDB.setDescription(role.getDescription());
        roleDB = this.roleRepository.save(roleDB);
        return roleDB;
    }

    public Role fetchRoleById(long id){
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if(roleOptional.isPresent()){
            return roleOptional.get();
        }
        return null;
    }

    public ResultPaginationDTO fetchAll(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageRole.getTotalPages());
        mt.setTotal(pageRole.getTotalElements());

        rs.setMeta(mt);

        rs.setResult(pageRole.getContent());

        return rs;
    }

    public void deleteById(long id){
        this.roleRepository.deleteById(id);
    }
}
