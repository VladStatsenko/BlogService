package main.repository;

import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Integer> {

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time<current_timestamp")
    Page<Post> findAllPosts(Pageable pageable);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() ORDER BY (SELECT sum(value) FROM post_votes c WHERE c.post_id = p.id) DESC", nativeQuery = true)
    Page<Post> findByLikes(Pageable pageable);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 AND p.moderation_status = 'ACCEPTED' " +
            "AND p.time < NOW() ORDER BY (SELECT count(*) FROM post_comments c WHERE c.post_id = p.id) DESC", nativeQuery = true)
    Page<Post> findByComments(Pageable pageable);

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.status = 'ACCEPTED' " +
            "AND p.time<current_timestamp AND p.title LIKE %:searchQuery%")
    Page<Post> postsSearch(Pageable pageable, @Param("searchQuery") String searchQuery);

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND" +
            " p.time> :from AND p.time< :to")
    List<Post> findPostsByYear(@Param("from") Date from, @Param("to") Date to);

    @Query(value = "SELECT p.time FROM Post p WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time<current_timestamp")
    List<Date> findAllPublicationDate();

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time<current_timestamp AND " +
            "p.time> :from AND p.time< :to")
    Page<Post> findByDate(Pageable pageable, Date from, Date to);

    @Query(value = "SELECT p FROM Post p JOIN p.tags t JOIN t.tag k WHERE k.name= :tag " +
            "AND p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time<current_timestamp")
    Page<Post> findByTag(Pageable pageable, @Param("tag") String tag);

    @Query(value = "SELECT p FROM Post p JOIN p.tags t JOIN t.tag k WHERE k.name= :tag " +
            "AND p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time<current_timestamp")
    List<Post> findByTag(@Param("tag") String tag);

    @Query(value = "SELECT p FROM Post p WHERE p.time<current_timestamp " +
            "AND p.id = :id")
    Optional<Post> findById(@Param("id") int id);

    @Query(value = "SELECT * FROM posts p JOIN users u ON u.id = p.user_id WHERE p.is_active = 1 " +
            "AND p.moderation_status = :status AND p.time<current_timestamp", nativeQuery = true)
    Page<Post> findPostModeration(@Param("status") String status, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM posts p WHERE " +
            "AND p.is_active = 1 AND p.moderation_status = 'NEW'", nativeQuery = true)
    int findPostIsModerate();

    @Query(value = "SELECT * FROM posts p JOIN users u ON u.id = p.user_id WHERE u.id = :id AND p.is_active = 1 " +
            "AND p.moderation_status = :status AND p.time<current_timestamp", nativeQuery = true)
    Page<Post> findMyActivePosts(Pageable pageable, @Param("status") String status, @Param("id") int id);

    @Query(value = "SELECT * FROM posts p JOIN users u ON u.id = p.user_id WHERE u.id = :id AND p.is_active = 0 " +
            "AND p.time<current_timestamp", nativeQuery = true)
    Page<Post> findMyInactivePosts(Pageable pageable, @Param("id") int active);

    @Query(value = "INSERT INTO posts (is_active, moderation_status, time, text, title, moderation_id, user_id, " +
            "view_count)  VALUES ( :active , 'NEW', :time, :text, :title, :user_id, :user_id, 0)", nativeQuery = true)
    Post addPost(@Param("active") int active, @Param("time") Date date, @Param("text") String text,
                 @Param("title") String title, @Param("user_id") int userId);

    @Query(value = "UPDATE posts p SET p.is_active=:active, p.moderation_status='NEW', p.time=:time, " +
            "p.text=:text, p.title=:title WHERE p.id=:id", nativeQuery = true)
    Post editPost(@Param("active") int active, @Param("time") Date date, @Param("text") String text,
                  @Param("title") String title, @Param("id") int id);

}
