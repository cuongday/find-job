package vn.ndc.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ndc.jobhunter.domain.Permission;
import vn.ndc.jobhunter.domain.User;
import vn.ndc.jobhunter.domain.response.ResultPaginationDTO;
import vn.ndc.jobhunter.service.PermissionService;
import vn.ndc.jobhunter.util.annotation.ApiMessage;
import vn.ndc.jobhunter.util.error.IdInvalidException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) throws IdInvalidException {
        if(this.permissionService.isPermissionExist(permission)) {
            throw new IdInvalidException("Permission đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.permissionService.handleCreatePermission(permission));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission) throws IdInvalidException {
        Permission fetchPermission = this.permissionService.fetchById(permission.getId());
        if(fetchPermission == null) {
            throw new IdInvalidException("Permission với id = " +permission.getId() +" không tồn tại");
        }

        if(this.permissionService.isPermissionExist(permission)) {
            if(this.permissionService.isSameName(permission)) {
                throw new IdInvalidException("Permission đã tồn tại");
            }
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(this.permissionService.handleUpdatePermission(permission));
    }

    @GetMapping("/permissions")
    @ApiMessage("Fetch all permissions")
    public ResponseEntity<ResultPaginationDTO> fetchAllPermission(
            @Filter Specification<Permission> permissionSpec,
            Pageable pageable
    ) {
        ResultPaginationDTO rs = this.permissionService.handleGetPermission(permissionSpec, pageable);
        return ResponseEntity.ok(rs);
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission by id")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") long id) throws IdInvalidException {
        Permission permission = this.permissionService.fetchById(id);
        if(permission == null) {
            throw new IdInvalidException("Permission với id = " + id + " không tồn tại");
        }
        this.permissionService.handleDeletePermission(id);
        return ResponseEntity.ok().body(null);
    }


}
