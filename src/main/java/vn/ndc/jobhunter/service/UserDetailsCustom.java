package vn.ndc.jobhunter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;


@Component("userDetailService")
@RequiredArgsConstructor
public class UserDetailsCustom implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        vn.ndc.jobhunter.domain.User user = this.userService.handleGetUserByUserName(username);

        if(user == null) {
            throw new UsernameNotFoundException("Username/ password không hợp lệ ");
        }
        return new User(
            user.getEmail(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
