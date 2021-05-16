package main.dao;

import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostDao {

    Page<Post> findAllPosts(Pageable pageable);

    Page<Post> findByLikes(Pageable pageable);

    Page<Post> findByComments(Pageable pageable);
}
