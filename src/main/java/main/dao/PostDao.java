package main.dao;

import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PostDao {

    Page<Post> findAllPosts(Pageable pageable);

    Page<Post> findByLikes(Pageable pageable);

    Page<Post> findByComments(Pageable pageable);

    Page<Post> postsSearch(Pageable pageable,String searchQuery);

    List<Post> findPostsByYear( Date from, Date to);

    List<Date> findAllPublicationDate();

    Page<Post> findByDate(Pageable pageable,Date from, Date to);

    Page<Post> findByTag(Pageable pageable, String tag);

    Optional<Post> findById(int id);

    Page<Post> findPostModeration(String status,Pageable pageable);

    Page<Post> findMyActivePosts(Pageable pageable,String status,int id);

    Page<Post> findMyInactivePosts(Pageable pageable,int active);

}
