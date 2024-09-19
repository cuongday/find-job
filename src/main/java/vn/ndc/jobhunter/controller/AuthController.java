package vn.ndc.jobhunter.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.ndc.jobhunter.domain.User;
import vn.ndc.jobhunter.domain.request.ReqLoginDTO;
import vn.ndc.jobhunter.domain.response.ResCreateUserDTO;
import vn.ndc.jobhunter.domain.response.ResLoginDTO;
import vn.ndc.jobhunter.service.UserService;
import vn.ndc.jobhunter.util.SecurityUtil;
import vn.ndc.jobhunter.util.annotation.ApiMessage;
import vn.ndc.jobhunter.util.error.IdInvalidException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${ndc.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        //Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());
        //xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // create a token
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResLoginDTO res = new ResLoginDTO();

        User currentUserDB = this.userService.handleGetUserByUserName(loginDTO.getUsername());
        if(currentUserDB != null){
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName(),
                    currentUserDB.getRole()
            );
            res.setUser(userLogin);
        }
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);


        res.setAccessToken(access_token);

        // create a refresh token
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);
        //update user
        this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

        //set cookies
        ResponseCookie resCookies = ResponseCookie.from("refresh_token", refresh_token)
                .httpOnly(true)
                .maxAge(refreshTokenExpiration)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);


    }


    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount(){
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ?
                SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUserDB = this.userService.handleGetUserByUserName(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount(userLogin);
        if(currentUserDB != null){
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(@CookieValue(name = "refresh_token", defaultValue = "default") String refreshToken) throws IdInvalidException {
        if(refreshToken.equals("default")){
            throw new IdInvalidException("Bạn không có Refresh token Cookie");
        }

        // check valid
        Jwt decodedToken =  this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();

        // check user by token and email
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refreshToken, email);
        if(currentUser == null){
            throw new IdInvalidException("Refresh token không hợp lệ");
        }



        //issue new token/set refresh token as cookies
        ResLoginDTO res = new ResLoginDTO();

        User currentUserDB = this.userService.handleGetUserByUserName(email);
        if(currentUserDB != null){
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName(),
                    currentUserDB.getRole()
            );
            res.setUser(userLogin);
        }
        String access_token = this.securityUtil.createAccessToken(email, res);


        res.setAccessToken(access_token);

        // create a refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);
        //update user
        this.userService.updateUserToken(new_refresh_token, email);

        //set cookies
        ResponseCookie resCookies = ResponseCookie.from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .maxAge(refreshTokenExpiration)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);

    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ?
                SecurityUtil.getCurrentUserLogin().get() : "";
        if(email.equals("")){
            throw new IdInvalidException("Access token không hợp lệ");
        }

        //update refresh token = null
        this.userService.updateUserToken(null, email);

        //remove refresh cookies
        ResponseCookie deleteSpringCookie = ResponseCookie.from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .build();
    }

    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User postmanUser) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(postmanUser.getEmail());
        if(isEmailExist){
            throw new IdInvalidException("Email " + postmanUser.getEmail() +" đã tồn tại, vui lòng chọn email khác");
        }

        String hashPassword = this.passwordEncoder.encode(postmanUser.getPassword());
        postmanUser.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(postmanUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }
}
