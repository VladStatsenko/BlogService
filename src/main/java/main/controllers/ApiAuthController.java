package main.controllers;

import main.api.request.body.LoginRequest;
import main.api.request.body.RegisterRequest;
import main.api.response.CaptchaResponse;
import main.api.response.LoginResponse;
import main.api.response.RegisterResponse;
import main.service.CaptchaService;
import main.service.LoginService;
import main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiAuthController {
    private final CaptchaService captchaService;
    private final UserService userService;
    private final LoginService loginService;


    @Autowired
    public ApiAuthController(CaptchaService captchaService, UserService userService, LoginService loginService) {
        this.captchaService = captchaService;
        this.userService = userService;
        this.loginService = loginService;
    }

    @PostMapping("/auth/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
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

    @PostMapping("/auth/register")
    public RegisterResponse register(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }


}
