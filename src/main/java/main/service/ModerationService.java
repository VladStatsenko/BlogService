package main.service;

import main.api.request.ModeratePostRequest;
import main.api.response.PostResponse;
import main.api.response.body.PostBody;
import main.dao.PostDao;
import main.model.Post;
import main.model.User;
import main.repository.PostRepository;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModerationService {

    private final PostDao postDao;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public ModerationService(PostDao postDao, UserRepository userRepository, PostRepository postRepository) {
        this.postDao = postDao;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    /**
     * Метод выводит все посты, которые требуют модерационных действий (которые нужно утвердить или
     * отклонить) или над которыми мною были совершены модерационные действия: которые я отклонил
     * или утвердил.
     * @param offset
     * @param limit
     * @param status
     * @param principal
     * @return
     */
    public PostResponse getPostsModeration(int offset, int limit, String status, Principal principal) {
        Pageable pageable;
        PostResponse postResponse = null;

        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        if (user.getIsModerator() == 1) {

            switch (status) {
                case ("new"):
                    pageable = PageRequest.of(offset, limit, Sort.by("time").descending());
                    postResponse = getPostsByStatus(Post.ModerationStatus.NEW.toString(), pageable);
                    break;
                case ("declined"):
                    pageable = PageRequest.of(offset, limit, Sort.by("time").ascending());
                    postResponse = getPostsByStatus(Post.ModerationStatus.DECLINED.toString(), pageable);
                    break;
                case ("accepted"):
                    pageable = PageRequest.of(offset, limit);
                    postResponse = getPostsByStatus(Post.ModerationStatus.ACCEPTED.toString(), pageable);
                    break;
                default:
                    postResponse = new PostResponse();
            }
        }
        return postResponse;
    }

    private PostResponse getPostsByStatus(String status, Pageable pageable) {

        List<PostBody> postDTOList = postDao
                .findPostModeration(status, pageable)
                .get()
                .map(PostBody::new)
                .collect(Collectors.toList());
        return new PostResponse(postDTOList.size(), postDTOList);
    }

    /**
     * Метод фиксирует действие модератора по посту: его утверждение или отклонение.
     * @param moderatePostRequest
     * @param principal
     * @return
     */
    public boolean moderatePost(ModeratePostRequest moderatePostRequest, Principal principal) {
        User moderator = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
        Post post = postRepository.findById(moderatePostRequest.getPostId()).orElse(null);

        if (post == null || moderator == null) {
            return false;
        } else {
            if (moderatePostRequest.getDecision().equals("accept")) {
                post.setStatus(Post.ModerationStatus.ACCEPTED);
            }
            if (moderatePostRequest.getDecision().equals("decline")) {
                post.setStatus(Post.ModerationStatus.DECLINED);
            }
            post.setModerator(moderator);
            postRepository.save(post);
        }

        return true;
    }

}
