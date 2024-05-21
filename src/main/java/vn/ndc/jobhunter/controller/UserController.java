package vn.ndc.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.ndc.jobhunter.domain.User;
import vn.ndc.jobhunter.domain.dto.ResUpdateUserDTO;
import vn.ndc.jobhunter.domain.dto.ResUserDTO;
import vn.ndc.jobhunter.domain.dto.ResultPaginationDTO;
import vn.ndc.jobhunter.domain.dto.ResCreateUserDTO;
import vn.ndc.jobhunter.service.UserService;
import vn.ndc.jobhunter.util.annotation.ApiMessage;
import vn.ndc.jobhunter.util.error.IdInvalidException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/users")
    @ApiMessage("Create new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(user.getEmail());
        if(isEmailExist) {
            throw new IdInvalidException(
                    "Email " + user.getEmail() + " đã tồn tại, vui lòng sử dụng email khác");
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }

    @GetMapping("/users/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") Long id) throws IdInvalidException {
        User fetchUser = this.userService.fetchUserById(id);
        if(fetchUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.userService.convertToResUserDTO(fetchUser));
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all user")
    public ResponseEntity<ResultPaginationDTO> fetchAllUser(
            @Filter Specification<User> userSpec,
            Pageable pageable
            ) {
        ResultPaginationDTO rs = this.userService.handleGetUser(userSpec, pageable);
        return ResponseEntity.ok(rs);
    }


    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete user by id")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id)
        throws IdInvalidException{
        User currentUser = this.userService.fetchUserById(id);
        if(currentUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}")
    @ApiMessage("Update user by id")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@PathVariable("id") Long id, @RequestBody User user) throws IdInvalidException {
        User userUpdate = this.userService.handleUpdateUser(user);
        if(userUpdate == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(userUpdate));
    }
}
