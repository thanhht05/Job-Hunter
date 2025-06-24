package vn.JobHunter.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.JobHunter.domain.User;
import vn.JobHunter.domain.dto.ResponeLoginDto;
import vn.JobHunter.domain.dto.LoginDto;
import vn.JobHunter.service.UserService;
import vn.JobHunter.util.SecurityUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.HttpHeadResponseDecorator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil, UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponeLoginDto> handleLogin(@Valid @RequestBody LoginDto uDto) {

        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                uDto.getEmail(), uDto.getPassword());
        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        String accessToken = this.securityUtil.createAccessToken(authentication);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponeLoginDto responeLoginDto = new ResponeLoginDto();
        User curUser = this.userService.fetchUserByUsername(uDto.getEmail());
        ResponeLoginDto.UserLogin userLogin = new ResponeLoginDto.UserLogin(curUser.getId(), curUser.getEmail(),
                curUser.getFullName());

        responeLoginDto.setUserLogin(userLogin);
        responeLoginDto.setAccessToken(accessToken);

        // create refresh token
        String refreshToke = this.securityUtil.createRefreshToken(uDto.getEmail(), responeLoginDto);
        // update user
        this.userService.updateUserToken(refreshToke, uDto.getEmail());

        // set cookie
        ResponseCookie responseCookie = ResponseCookie
                .from("refreshToken", refreshToke)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(responeLoginDto);

    }
}
