package main.api.response.body;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.api.response.OnePostResponse;
import main.model.Post;
import main.model.PostComment;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentBody {
    private int id;
    private long timestamp;
    private String text;
    private UserBody user;

    public CommentBody(PostComment postComment){
        this.id = postComment.getId();
        this.timestamp = (postComment.getTime().getTime()/1000);
        this.text = postComment.getText();
        this.user = new UserBody(postComment.getUser());
    }
}
