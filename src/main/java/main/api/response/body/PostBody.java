package main.api.response.body;

import lombok.Data;

@Data
public class PostBody {
    private int id;
    private int timestamp;
    private UserBody user;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
}
