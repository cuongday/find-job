package vn.ndc.jobhunter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.ndc.jobhunter.domain.Company;
import vn.ndc.jobhunter.domain.Role;
import vn.ndc.jobhunter.domain.User;
import vn.ndc.jobhunter.domain.response.ResCreateUserDTO;
import vn.ndc.jobhunter.domain.response.ResUpdateUserDTO;
import vn.ndc.jobhunter.domain.response.ResUserDTO;
import vn.ndc.jobhunter.domain.response.ResultPaginationDTO;
import vn.ndc.jobhunter.repository.CompanyRepository;
import vn.ndc.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleService roleService;


    public User handleCreateUser(User user) {

        if(user.getCompany() != null){
            Optional<Company> companyOptional = this.companyRepository.findById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }

        if(user.getRole() != null){
            Role r = this.roleService.fetchRoleById(user.getRole().getId());
            user.setRole(r != null ? r : null);
        }

        return this.userRepository.save(user);
    }

    public void handleDeleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    public User fetchUserById(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public ResultPaginationDTO handleGetUser(Specification<User> userSpec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(userSpec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        rs.setMeta(meta);

        List<ResUserDTO> listUser  = pageUser.getContent()
                        .stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listUser);

        return rs;
    }

    public User handleUpdateUser(User reqUser) {
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser!=null) {
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setGender(reqUser.getGender());
            currentUser.setAge(reqUser.getAge());
            currentUser.setName(reqUser.getName());
        }
        if(reqUser.getCompany() != null){
            Optional<Company> companyOptional = this.companyRepository.findById(reqUser.getCompany().getId());
            currentUser.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }

        if(reqUser.getRole() != null){
            Role r = this.roleService.fetchRoleById(reqUser.getRole().getId());
            currentUser.setRole(r != null ? r : null);
        }

        currentUser = this.userRepository.save(currentUser);
        return currentUser;
    }

    public User handleGetUserByUserName(String username) {
        return this.userRepository.findByEmail(username);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
        ResCreateUserDTO.Company company = new ResCreateUserDTO.Company() ;

        resCreateUserDTO.setId(user.getId());
        resCreateUserDTO.setEmail(user.getEmail());
        resCreateUserDTO.setName(user.getName());
        resCreateUserDTO.setAge(user.getAge());
        resCreateUserDTO.setCreatedAt(user.getCreatedAt());
        resCreateUserDTO.setGender(user.getGender());
        resCreateUserDTO.setAddress(user.getAddress());

        if(user.getCompany() != null){
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            resCreateUserDTO.setCompany(company);
        }
        return resCreateUserDTO;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO resUpdateUserDTO = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser companyUser = new ResUpdateUserDTO.CompanyUser();

        resUpdateUserDTO.setId(user.getId());
        resUpdateUserDTO.setName(user.getName());
        resUpdateUserDTO.setAge(user.getAge());
        resUpdateUserDTO.setUpdatedAt(user.getUpdatedAt());
        resUpdateUserDTO.setGender(user.getGender());
        resUpdateUserDTO.setAddress(user.getAddress());

        if (user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            resUpdateUserDTO.setCompany(companyUser);
        }

        return resUpdateUserDTO;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO resUserDTO = new ResUserDTO();
        ResUserDTO.CompanyUser companyUser = new ResUserDTO.CompanyUser();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();

        resUserDTO.setId(user.getId());
        resUserDTO.setEmail(user.getEmail());
        resUserDTO.setName(user.getName());
        resUserDTO.setAge(user.getAge());
        resUserDTO.setUpdatedAt(user.getUpdatedAt());
        resUserDTO.setCreatedAt(user.getCreatedAt());
        resUserDTO.setGender(user.getGender());
        resUserDTO.setAddress(user.getAddress());

        if(user.getCompany() != null){
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            resUserDTO.setCompany(companyUser);
        }

        if(user.getRole() != null){
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            resUserDTO.setRole(roleUser);
        }


        return resUserDTO;
    }

    public void updateUserToken(String token, String email){
        User currentUser = this.handleGetUserByUserName(email);
        if(currentUser != null){
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email){
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
