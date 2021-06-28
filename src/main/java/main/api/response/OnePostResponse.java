package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.api.response.body.CommentBody;
import main.api.response.body.PostBody;
import main.api.response.body.UserBody;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnePostResponse extends PostBody {
    private String text;
    private List<CommentBody> comments;
    private List<String> tags;


    public OnePostResponse(int id, long timestamp, UserBody user, String title, String announce, int likeCount, int dislikeCount, int commentCount, int viewCount, List<CommentBody> comments, List<String> tags,String text) {
        super(id, timestamp, user, title, announce, likeCount, dislikeCount, commentCount, viewCount);
        this.comments = comments;
        this.tags = tags;
        this.text = text;

    }

}
