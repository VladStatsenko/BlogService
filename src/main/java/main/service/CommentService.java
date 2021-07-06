package main.service;

import main.api.request.CommentRequest;
import main.api.response.CommentResponse;
import main.api.response.error.ErrorBody;
import main.api.response.error.FalseResponse;
import main.model.PostComment;
import main.model.User;
import main.repository.CommentRepository;
import main.repository.PostRepository;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;

@Service
public class CommentService {

    private final int MINIMAL_TEXT_LENGTH = 5;

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentService(UserRepository userRepository, CommentRepository commentRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    /**
     * Метод добавляет комментарий к посту.
     * @param commentRequest
     * @param principal
     * @return
     */
    public ResponseEntity<Object> postComment(CommentRequest commentRequest, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        ErrorBody error = new ErrorBody();
        PostComment postComment = new PostComment();
        if (commentRequest.getText().length() > MINIMAL_TEXT_LENGTH) {
            postComment.setText(commentRequest.getText());
        } else {
            FalseResponse falseResponse = new FalseResponse();
            falseResponse.setResult(false);
            error.setText("Текст не комментария не задан или слишком короткий");
            falseResponse.setErrors(error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(falseResponse);
        }
        postComment.setPost(postRepository.findById(commentRequest.getPostId()).orElse(null));
        postComment.setParent(commentRepository.findById(commentRequest.getParentId()).orElse(null));
        postComment.setUser(user);
        postComment.setTime(new Date());
        commentRepository.save(postComment);
        return ResponseEntity.ok(new CommentResponse(postComment.getId()));
    }

}
