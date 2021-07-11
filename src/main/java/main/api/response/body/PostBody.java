package main.api.response.body;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.Post;
import org.jsoup.Jsoup;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostBody {
    private int id;
    private long timestamp;
    private UserBody user;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;

    public PostBody(Post post) {
        this.id = post.getId();
        this.timestamp = (post.getTime().getTime() / 1000);
        this.user = new UserBody(post.getUser());
        this.title = post.getTitle();
        this.announce = htmlTags(post.getText()).length() < 150 ? htmlTags(post.getText()) : htmlTags(post.getText())
                .substring(0, 150).concat("...");
        this.likeCount = post.getLikes().size();
        this.dislikeCount = post.getDislikes().size();
        this.commentCount = post.getComments().size();
        this.viewCount = post.getViewCount();
    }

    private static String htmlTags(String text){
        return Jsoup.parse(text).text();
    }
}
