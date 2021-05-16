package main.dao.impl;

import main.dao.PostDao;
import main.model.Post;
import main.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PostDaoImpl implements PostDao {

    private final PostRepository postRepository;

    @Autowired
    public PostDaoImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    @Override
    public Page<Post> findAllPosts(Pageable pageable) {
        return postRepository.findAllPosts(pageable);
    }

    @Override
    public Page<Post> findByLikes(Pageable pageable) {
        return postRepository.findByLikes(pageable);
    }

    @Override
    public Page<Post> findByComments(Pageable pageable) {
        return postRepository.findByComments(pageable);
    }
}
