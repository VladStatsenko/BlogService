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

    @Query(value = "SELECT distinct p FROM PostVoters v JOIN v.post p " +
            "GROUP BY p ORDER BY COUNT (v) DESC")
    Page<Post> findByLikes(Pageable pageable);

    @Query(value = "SELECT distinct p FROM PostComment c JOIN c.post p " +
            "GROUP BY p ORDER BY COUNT (c) DESC")
    Page<Post> findByComments(Pageable pageable);

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.status = 'ACCEPTED' " +
            "AND p.time<current_timestamp AND p.title LIKE %:searchQuery%")
    Page<Post> postsSearch(Pageable pageable , @Param("searchQuery") String searchQuery);

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND" +
            " p.time> :from AND p.time< :to")
    List<Post> findPostsByYear(@Param("from") Date from , @Param("to") Date to);

    @Query(value = "SELECT p.time FROM Post p WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time<current_timestamp")
    List<Date> findAllPublicationDate();

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time<current_timestamp AND " +
            "p.time> :from AND p.time< :to")
    Page<Post> findByDate(Pageable pageable,Date from, Date to);

    @Query(value = "SELECT p FROM Post p JOIN p.tags t JOIN t.tag k WHERE k.name= :tag " +
            "AND p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time<current_timestamp")
    Page<Post> findByTag(Pageable pageable, @Param("tag") String tag);

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time<current_timestamp " +
            "AND p.id = :id")
    Optional<Post> findById(@Param("id") int id);

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1")
    Page<Post> findPostsModeration(Pageable pageable);

}
