package main.service;

import main.api.request.LikeRequest;
import main.api.response.VoteResponse;
import main.model.Post;
import main.model.PostVoters;
import main.model.User;
import main.repository.PostRepository;
import main.repository.UserRepository;
import main.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;

@Service
public class PostVoteService {
    private final int LIKE_VALUE = 1;
    private final int DISLIKE_VALUE = -1;

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostVoteService(VoteRepository voteRepository, PostRepository postRepository, UserRepository userRepository) {
        this.voteRepository = voteRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    /**
     * Метод сохраняет в таблицу post_votes лайк текущего авторизованного пользователя. В случае
     * повторного лайка возвращает {result: false}.
     * @param likeRequest
     * @param principal
     * @return
     */
    public VoteResponse likePost(LikeRequest likeRequest, Principal principal) {
        return new VoteResponse(votePost(likeRequest, principal, LIKE_VALUE));
    }

    /**
     * Метод сохраняет в таблицу post_votes дизлайк текущего авторизованного пользователя. В случае
     * повторного дизлайка возвращает {result: false}.
     * @param likeRequest
     * @param principal
     * @return
     */
    public VoteResponse dislikePost(LikeRequest likeRequest, Principal principal) {
        return new VoteResponse(votePost(likeRequest, principal, DISLIKE_VALUE));
    }

    private boolean votePost(LikeRequest likeRequest, Principal principal, int value) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        Post post = postRepository.findById(likeRequest.getPostId()).orElse(null);

        if (post==null || user==null){
            return false;
        }

        PostVoters postVoters = voteRepository.findVoteByUserAndPost(user, post);
        if (postVoters == null) {
            postVoters = new PostVoters();
            postVoters.setUser(user);
            postVoters.setPost(post);
            postVoters.setTime(new Date());
            postVoters.setValue(value);
            voteRepository.save(postVoters);
            return true;
        } else {
            if (postVoters.getValue() != value){
                postVoters.setTime(new Date());
                postVoters.setValue(value);
                voteRepository.save(postVoters);
                return true;
            }
        }
        return false;
    }
}
