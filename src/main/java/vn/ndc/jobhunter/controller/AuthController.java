package vn.ndc.jobhunter.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.ndc.jobhunter.domain.User;
import vn.ndc.jobhunter.domain.dto.LoginDTO;
import vn.ndc.jobhunter.domain.dto.ResLoginDTO;
import vn.ndc.jobhunter.service.UserService;
import vn.ndc.jobhunter.util.SecurityUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        //Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());
        //xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // create a token
        String access_token = this.securityUtil.createToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResLoginDTO res = new ResLoginDTO();

        User currentUserDB = this.userService.handleGetUserByUserName(loginDTO.getUsername());
        if(currentUserDB != null){
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName()
            );
            res.setUser(userLogin);
        }

        res.setAccessToken(access_token);

        return ResponseEntity.ok().body(res);
    }
}
