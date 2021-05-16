package main.service;

import main.api.response.PostResponse;
import main.api.response.body.PostBody;
import main.dao.PostDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostDao postDao;

    @Autowired
    public PostService(PostDao postDao) {
        this.postDao = postDao;
    }

    public PostResponse getPosts(int offset, int limit, String mode) {
        Pageable pageable;
        PostResponse postResponse = null;

        switch (mode) {
            case ("recent"):
                pageable = PageRequest.of(offset, limit, Sort.by("time").descending());
                postResponse = getPostsSortedByTime(pageable);
                break;
            case ("early"):
                pageable = PageRequest.of(offset, limit, Sort.by("time").ascending());
                postResponse = getPostsSortedByTime(pageable);
                break;
            case ("best"):
                pageable = PageRequest.of(offset, limit);
                postResponse = getPostsSortedByLikes(pageable);
                break;
            case ("popular"):
                pageable = PageRequest.of(offset, limit);
                postResponse = getPostsSortedByComments(pageable);
                break;
            default:
                postResponse = new PostResponse();
        }
        return postResponse;
    }

    private PostResponse getPostsSortedByTime(Pageable pageable) {
        List<PostBody> postDTOList = postDao
                .findAllPosts(pageable)
                .get()
                .map(PostBody::new)
                .collect(Collectors.toList());
        return new PostResponse(postDTOList.size(), postDTOList);
    }

    private PostResponse getPostsSortedByLikes(Pageable pageable) {
        List<PostBody> postDTOList = postDao
                .findByLikes(pageable)
                .get()
                .map(PostBody::new)
                .collect(Collectors.toList());
        return new PostResponse(postDTOList.size(), postDTOList);
    }

    private PostResponse getPostsSortedByComments(Pageable pageable) {
        List<PostBody> postDTOList = postDao
                .findByComments(pageable)
                .get()
                .map(PostBody::new)
                .collect(Collectors.toList());
        return new PostResponse(postDTOList.size(), postDTOList);
    }
}
