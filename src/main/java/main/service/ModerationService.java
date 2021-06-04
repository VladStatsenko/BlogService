package main.service;

import main.api.response.PostResponse;
import main.api.response.body.PostBody;
import main.dao.PostDao;
import main.model.Post;
import main.model.User;
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

    @Autowired
    public ModerationService(PostDao postDao, UserRepository userRepository) {
        this.postDao = postDao;
        this.userRepository = userRepository;
    }

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

}
