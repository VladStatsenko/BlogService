package main.controllers;

import main.api.request.ChangePassRequest;
import main.api.request.LoginRequest;
import main.api.request.RegisterRequest;
import main.api.request.RestoreRequest;
import main.api.response.CaptchaResponse;
import main.api.response.ChangePassResponse;
import main.api.response.LoginResponse;
import main.api.response.RegisterResponse;
import main.repository.UserRepository;
import main.service.CaptchaService;
import main.service.LoginService;
import main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiAuthController {
    private final CaptchaService captchaService;
    private final UserService userService;
    private final LoginService loginService;
    private final UserRepository userRepository;


    @Autowired
    public ApiAuthController(CaptchaService captchaService, UserService userService, LoginService loginService, UserRepository userRepository) {
        this.captchaService = captchaService;
        this.userService = userService;
        this.loginService = loginService;
        this.userRepository = userRepository;
    }

    @PostMapping("/auth/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest){
        if (!userRepository.findByEmail(loginRequest.getEmail()).isPresent()){
            return new LoginResponse();
        }
        return loginService.login(loginRequest);
    }


    @GetMapping("/auth/check")
    public LoginResponse check(Principal principal) {
        return loginService.check(principal);
    }

    @GetMapping("/auth/captcha")
    public CaptchaResponse captcha() {
        return captchaService.getCaptcha();
    }

    @GetMapping("auth/logout")
    @PreAuthorize(value = "hasAuthority('user:write')")
    public LoginResponse logout(HttpServletRequest request, HttpServletResponse response,Principal principal) {
        loginService.logout(request, response);
        return loginService.check(principal);
    }

    @PostMapping("/auth/register")
    public RegisterResponse register(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/auth/restore")
    public Boolean restore(@RequestBody RestoreRequest request,HttpServletRequest httpServletRequest) {
        return userService.RestorePass(request,httpServletRequest);
    }

    @PostMapping("/auth/password")
    public ChangePassResponse changePassword(@RequestBody ChangePassRequest request) {
        return userService.changePassword(request);
    }




}
