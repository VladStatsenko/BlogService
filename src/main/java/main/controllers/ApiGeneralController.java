package main.controllers;

import main.api.request.CommentRequest;
import main.api.request.EditProfileRequest;
import main.api.request.SettingsRequest;
import main.api.response.*;
import main.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {
    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final TagService tagService;
    private final GeneralService generalService;
    private final StorageService storageService;
    private final CommentService commentService;
    private final UserService userService;
    private final PostService postService;

    @Autowired

    public ApiGeneralController(InitResponse initResponse, SettingsService settingsService, TagService tagService, GeneralService generalService, StorageService storageService, CommentService commentService, UserService userService, PostService postService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.tagService = tagService;
        this.generalService = generalService;
        this.storageService = storageService;
        this.commentService = commentService;
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
    public SettingsResponse settings() {
        return settingsService.getGlobalSettings();
    }

    @PutMapping("/settings")
    @PreAuthorize(value = "hasAuthority('user:moderate')")
    public SettingsResponse putSettings(@RequestBody SettingsRequest request, Principal principal) {
        return settingsService.putSettings(request, principal);
    }

    @GetMapping("/tag")
    public TagResponse tag() {
        return tagService.getTags();
    }

    @GetMapping("/calendar")
    public CalendarResponse getCalendar(Integer year) {
        return generalService.getPostsByYear(year);
    }

    @PostMapping(value = "/image", consumes = "multipart/form-data")
    @PreAuthorize(value = "hasAuthority('user:write')")
    public ResponseEntity<Object> uploadPhoto(@RequestParam("image") MultipartFile photo) {
        return storageService.uploadPhoto(photo);
    }

    @PostMapping("/comment")
    @PreAuthorize(value = "hasAuthority('user:write')")
    public Object postComment(@RequestBody CommentRequest commentRequest, Principal principal) {
        return commentService.postComment(commentRequest, principal);
    }

    @PostMapping(value = "/profile/my",consumes = {"multipart/form-data"})
    @PreAuthorize(value = "hasAuthority('user:write')")
    public EditProfileResponse editProfile(@RequestPart("photo") MultipartFile photo,
                                           @ModelAttribute EditProfileRequest editProfileRequest, Principal principal) {
        return userService.editProfile(photo,editProfileRequest, principal);
    }

    @GetMapping("/statistics/my")
    @PreAuthorize(value = "hasAuthority('user:write')")
    public StatisticResponse getStatistic (Principal principal) {
        return userService.getStatistic(principal);
    }

    @GetMapping("/statistics/all")
    public StatisticResponse getAllStatistic () {
        return postService.getAllStatistic();
    }

}
