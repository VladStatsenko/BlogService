package main.controllers;

import main.api.response.*;
import main.service.CheckService;
import main.service.PostService;
import main.service.SettingsService;
import main.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {
    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final CheckService checkService;
    private final PostService postService;
    private final TagService tagService;

    public ApiGeneralController(InitResponse initResponse, SettingsService settingsService,
                                CheckService checkService, PostService postService, TagService tagService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.checkService = checkService;
        this.postService = postService;
        this.tagService = tagService;
    }

    @GetMapping("/init")
    private InitResponse init(){
        return initResponse;
    }

    @GetMapping("/settings")
    private SettingsResponse settings(){
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/auth/check")
    private CheckResponse check(){
        return checkService.GetCheck();
    }

    @GetMapping("/post")
    private PostResponse post(){
        return postService.post();
    }

    @GetMapping("/tag")
    private TagResponse tag(){
        return tagService.tags();
    }
}
