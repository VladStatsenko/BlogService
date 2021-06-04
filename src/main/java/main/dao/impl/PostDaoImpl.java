package main.dao.impl;

import main.dao.PostDao;
import main.model.Post;
import main.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Page<Post> postsSearch(Pageable pageable, String searchQuery) {
        return postRepository.postsSearch(pageable, searchQuery);
    }

    @Override
    public List<Post> findPostsByYear(Date from, Date to) {
        return postRepository.findPostsByYear(from, to);
    }

    @Override
    public List<Date> findAllPublicationDate() {
        return postRepository.findAllPublicationDate();
    }

    @Override
    public Page<Post> findByDate(Pageable pageable, Date from, Date to) {
        return postRepository.findByDate(pageable, from, to);
    }

    @Override
    public Page<Post> findByTag(Pageable pageable, String tag) {
        return postRepository.findByTag(pageable, tag);
    }

    @Override
    public Optional<Post> findById(int id) {
        return postRepository.findById(id);
    }

    @Override
    public Page<Post> findPostModeration(String status, Pageable pageable) {
        return postRepository.findPostModeration(status, pageable);
    }

    @Override
    public Page<Post> findMyActivePosts(Pageable pageable, String status, int id) {
        return postRepository.findMyActivePosts(pageable, status, id);
    }

    @Override
    public Page<Post> findMyInactivePosts(Pageable pageable, int active) {
        return postRepository.findMyInactivePosts(pageable, active);
    }

}
