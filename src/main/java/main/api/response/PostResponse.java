package main.api.response;

import lombok.Data;
import main.api.response.body.PostBody;

import java.util.List;

@Data
public class PostResponse {
    private int count;
    private List<PostBody> posts;

}
