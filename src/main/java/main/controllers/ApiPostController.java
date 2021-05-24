package main.controllers;

import main.api.response.OnePostResponse;
import main.api.response.PostResponse;
import main.service.PostService;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/post/search")
    private PostResponse searchPost(@RequestParam(required = false, defaultValue = "0") Integer offset,
                                    @RequestParam(required = false, defaultValue = "10") Integer limit,
                                    @RequestParam(defaultValue = "recent") String query) {
        return postService.getPostsBySearch(offset, limit, query);
    }

    @GetMapping("/post/byDate")
    private PostResponse searchPostByDate(@RequestParam(required = false, defaultValue = "0") Integer offset,
                                          @RequestParam(required = false, defaultValue = "10") Integer limit,
                                          @RequestParam(defaultValue = "recent") String date) {
        return postService.getPostsByDate(offset, limit, date);
    }

    @GetMapping("/post/byTag")
    private PostResponse findByTag(@RequestParam(required = false, defaultValue = "0") Integer offset,
                                   @RequestParam(required = false, defaultValue = "10") Integer limit,
                                   @RequestParam(defaultValue = "recent") String tag) {
        return postService.getPostsByTag(offset, limit, tag);
    }

    @GetMapping("/post/{id}")
    private OnePostResponse findById(@PathVariable int id) {
        return postService.getPostById(id);
    }

}
