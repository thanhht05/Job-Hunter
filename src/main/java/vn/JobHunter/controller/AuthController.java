package vn.JobHunter.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import vn.JobHunter.domain.User;
import vn.JobHunter.domain.request.ReqLoginDto;
import vn.JobHunter.domain.respone.ResponeLoginDto;
import vn.JobHunter.domain.respone.ResponeLoginDto.UserLogin;
import vn.JobHunter.service.UserService;
import vn.JobHunter.util.SecurityUtil;
import vn.JobHunter.util.annotation.ApiMessage;
import vn.JobHunter.util.exception.IdInvalidException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.HttpHeadResponseDecorator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        @PostMapping("/auth/login")
        public ResponseEntity<ResponeLoginDto> handleLogin(@Valid @RequestBody ReqLoginDto uDto) {

                // Nạp input gồm username/password vào Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                uDto.getUsername(), uDto.getPassword());
                // xác thực người dùng => cần viết hàm loadUserByUsername
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);

                // set user's login imformation into context ( can be used later)
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // set result login
                ResponeLoginDto responeLoginDto = new ResponeLoginDto();
                User curUser = this.userService.fetchUserByUsername(uDto.getUsername());
                ResponeLoginDto.UserLogin userLogin = new ResponeLoginDto.UserLogin(curUser.getId(), curUser.getEmail(),
                                curUser.getName());

                // create access token
                String accessToken = this.securityUtil.createAccessToken(authentication.getName(), userLogin);
                responeLoginDto.setAccessToken(accessToken);
                responeLoginDto.setUser(userLogin);

                // create refresh token
                String refreshToke = this.securityUtil.createRefreshToken(uDto.getUsername(), responeLoginDto);
                // update user
                this.userService.updateUserToken(refreshToke, uDto.getUsername());

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

        @GetMapping("/auth/account")
        @ApiMessage("Fetch account")
        public ResponseEntity<ResponeLoginDto.UserGetAccount> handleGetAccount() {

                // String email = SecurityUtil.getCurrentUserLogin()
                // .isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
                // standard java 8
                String email = SecurityUtil.getCurrentUserLogin().orElse("");
                User userDb = this.userService.fetchUserByUsername(email);
                ResponeLoginDto.UserGetAccount userGetAccount = new ResponeLoginDto.UserGetAccount();

                ResponeLoginDto.UserLogin userLogin = new UserLogin(userDb.getId(), userDb.getEmail(),
                                userDb.getName());

                userGetAccount.setUser(userLogin);
                return ResponseEntity.ok().body(userGetAccount);
        }

        @GetMapping("/auth/refresh")
        @ApiMessage("Get user by refresh token")
        public ResponseEntity<ResponeLoginDto> handleGetRefreshToken(
                        @CookieValue(name = "refreshToken", defaultValue = "defaultToken") String refreshToken)
                        throws IdInvalidException {
                if (refreshToken.equals("defaultToken")) {
                        throw new IdInvalidException("Khong co token o cookie");
                }
                // check token
                Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);

                String email = decodedToken.getSubject();

                User curUser = this.userService.getUserByRefreshTokenAndEmail(refreshToken, email);
                if (curUser == null) {
                        throw new IdInvalidException("Refresh token invalid");
                }

                // issue new token
                ResponeLoginDto responeLoginDto = new ResponeLoginDto();
                ResponeLoginDto.UserLogin userLogin = new ResponeLoginDto.UserLogin(curUser.getId(), curUser.getEmail(),
                                curUser.getName());
                String accessToken = this.securityUtil.createAccessToken(email, userLogin);
                responeLoginDto.setUser(userLogin);
                responeLoginDto.setAccessToken(accessToken);

                // create refresh token
                String newRefreshToken = this.securityUtil.createRefreshToken(email, responeLoginDto);
                // update user
                this.userService.updateUserToken(newRefreshToken, email);

                // set cookie
                ResponseCookie responseCookie = ResponseCookie
                                .from("refreshToken", newRefreshToken)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                                .body(responeLoginDto);
        }

        @PostMapping("/auth/logout")
        @ApiMessage("Logout")
        public ResponseEntity<Void> handelLogout() throws IdInvalidException {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";
                if (email.equals("")) {
                        throw new IdInvalidException("AccessToken invalid");
                }

                // update refresh token
                this.userService.updateUserToken(null, email);

                // delete cookie
                ResponseCookie deleteCookie = ResponseCookie
                                .from("refreshToken", null)
                                .httpOnly(true)
                                .path("/")
                                .secure(true)
                                .maxAge(0)
                                .build();

                return ResponseEntity.status(HttpStatus.CREATED)
                                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                                .body(null);

        }
}
