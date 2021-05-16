package main.controllers;

import main.api.response.CheckResponse;
import main.service.CheckService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiAuthController {
    private final CheckService checkService;

    public ApiAuthController(CheckService checkService) {
        this.checkService = checkService;
    }


    @GetMapping("/auth/check")
    private CheckResponse check(){
        return checkService.GetCheck();
    }
}
