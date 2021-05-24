package main.controllers;

import main.api.request.body.RegisterRequest;
import main.api.response.CaptchaResponse;
import main.api.response.CheckResponse;
import main.api.response.RegisterResponse;
import main.service.CaptchaService;
import main.service.CheckService;
import main.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiAuthController {
    private final CheckService checkService;
    private final CaptchaService captchaService;
    private final UserService userService;

    public ApiAuthController(CheckService checkService, CaptchaService captchaService, UserService userService) {
        this.checkService = checkService;
        this.captchaService = captchaService;
        this.userService = userService;
    }


    @GetMapping("/auth/check")
    private CheckResponse check() {
        return checkService.GetCheck();
    }

    @GetMapping("/auth/captcha")
    private CaptchaResponse captcha() {
        return captchaService.getCaptcha();
    }

    @PostMapping("/auth/register")
    private RegisterResponse register(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }
}
