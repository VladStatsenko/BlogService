package main.service;

import main.api.response.PostResponse;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    public PostResponse post(){
        PostResponse postResponse = new PostResponse();
        postResponse.getCount();
        postResponse.getPosts();
        return postResponse;
    }
}
