package main.controllers;

import main.api.response.OnePostResponse;
import main.api.response.PostResponse;
import main.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiPostController {
    private final PostService postService;
    @Autowired
    public ApiPostController(PostService postService) {
        this.postService = postService;
    }


    @GetMapping("/post")
    public PostResponse post(@RequestParam(required = false, defaultValue = "0") Integer offset,
                              @RequestParam(required = false, defaultValue = "10") Integer limit,
                              @RequestParam(required = false, defaultValue = "recent") String mode) {
        return postService.getPosts(offset, limit, mode);
    }

    @GetMapping("/post/search")
    public PostResponse searchPost(@RequestParam(required = false, defaultValue = "0") Integer offset,
                                    @RequestParam(required = false, defaultValue = "10") Integer limit,
                                    @RequestParam(defaultValue = "recent") String query) {
        return postService.getPostsBySearch(offset, limit, query);
    }

    @GetMapping("/post/byDate")
    public PostResponse searchPostByDate(@RequestParam(required = false, defaultValue = "0") Integer offset,
                                          @RequestParam(required = false, defaultValue = "10") Integer limit,
                                          @RequestParam(defaultValue = "recent") String date) {
        return postService.getPostsByDate(offset, limit, date);
    }

    @GetMapping("/post/byTag")
    public PostResponse findByTag(@RequestParam(required = false, defaultValue = "0") Integer offset,
                                   @RequestParam(required = false, defaultValue = "10") Integer limit,
                                   @RequestParam(defaultValue = "recent") String tag) {
        return postService.getPostsByTag(offset, limit, tag);
    }

    @GetMapping("/post/{id}")
    @PreAuthorize(value = "hasAuthority('user:write')")
    public ResponseEntity<OnePostResponse> findById(@PathVariable int id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

}
