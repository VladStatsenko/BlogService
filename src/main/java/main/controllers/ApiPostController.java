package main.controllers;

import main.api.request.LikeRequest;
import main.api.request.ModeratePostRequest;
import main.api.request.PostRequest;
import main.api.response.AddPostResponse;
import main.api.response.VoteResponse;
import main.api.response.OnePostResponse;
import main.api.response.PostResponse;
import main.service.ModerationService;
import main.service.PostService;
import main.service.PostVoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiPostController {
    private final PostService postService;
    private final ModerationService moderationService;
    private final PostVoteService postVoteService;

    @Autowired
    public ApiPostController(PostService postService, ModerationService moderationService, PostVoteService postVoteService) {
        this.postService = postService;
        this.moderationService = moderationService;
        this.postVoteService = postVoteService;
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
    public ResponseEntity<OnePostResponse> findById(@PathVariable int id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/post/moderation")
    @PreAuthorize(value = "hasAuthority('user:moderate')")
    public ResponseEntity<PostResponse> findPostByStatus(@RequestParam(required = false, defaultValue = "0") Integer offset,
                                                         @RequestParam(required = false, defaultValue = "10") Integer limit,
                                                         @RequestParam(defaultValue = "accepted") String status,
                                                         Principal principal) {
        return ResponseEntity.ok(moderationService.getPostsModeration(offset, limit, status, principal));

    }

    @GetMapping("/post/my")
    @PreAuthorize(value = "hasAuthority('user:write')")
    public ResponseEntity<PostResponse> findMyPosts(@RequestParam(required = false, defaultValue = "0") Integer offset,
                                                    @RequestParam(required = false, defaultValue = "10") Integer limit,
                                                    @RequestParam(defaultValue = "accepted") String status,
                                                    Principal principal) {
        return ResponseEntity.ok(postService.getMyPosts(offset, limit, status, principal));

    }
    @PostMapping("/post")
    @PreAuthorize(value = "hasAuthority('user:write')")
    public ResponseEntity<AddPostResponse> addPost(@RequestBody PostRequest postRequest, Principal principal){
        return ResponseEntity.ok(postService.addPost(postRequest,principal));
    }

    @PutMapping("/post/{id}")
    @PreAuthorize(value = "hasAuthority('user:write')")
    public ResponseEntity<AddPostResponse> editPost(@RequestBody PostRequest postRequest,@PathVariable int id,Principal principal){
        return ResponseEntity.ok(postService.editPost(postRequest,id,principal));
    }

    @PostMapping("/moderation")
    @PreAuthorize(value = "hasAuthority('user:moderate')")
    public ResponseEntity<Boolean> moderatePost(@RequestBody ModeratePostRequest moderatePostRequest, Principal principal){
        return ResponseEntity.ok(moderationService.moderatePost(moderatePostRequest,principal));
    }

    @PostMapping("/post/like")
    @PreAuthorize(value = "hasAuthority('user:write')")
    public VoteResponse likePost(@RequestBody LikeRequest likeRequest, Principal principal){
        return postVoteService.likePost(likeRequest,principal);
    }

    @PostMapping("/post/dislike")
    @PreAuthorize(value = "hasAuthority('user:write')")
    public VoteResponse dislikePost(@RequestBody LikeRequest likeRequest, Principal principal){
        return postVoteService.dislikePost(likeRequest,principal);
    }


}
