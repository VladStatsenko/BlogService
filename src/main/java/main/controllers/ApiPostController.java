package main.controllers;

import main.api.response.PostResponse;
import main.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiPostController {
    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/post")
    private PostResponse post(@RequestParam(required = false, defaultValue = "0") Integer offset,
                              @RequestParam(required = false, defaultValue = "10") Integer limit,
                              @RequestParam(required = false, defaultValue = "recent") String mode) {
        return postService.getPosts(offset, limit, mode);
    }
}
