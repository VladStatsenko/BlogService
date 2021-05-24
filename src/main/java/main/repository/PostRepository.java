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

    @Query(value = "select p from Post p where p.isActive = 1 and p.status = 'ACCEPTED' and p.time<current_timestamp")
    Page<Post> findAllPosts(Pageable pageable);

    @Query(value = "select distinct p from PostVoters v join v.post p " +
            "group by p order by count (v) desc")
    Page<Post> findByLikes(Pageable pageable);

    @Query(value = "select distinct p from PostComment c join c.post p " +
            "group by p order by count (c) desc")
    Page<Post> findByComments(Pageable pageable);

    @Query(value = "select p from Post p where p.isActive = 1 and p.status = 'ACCEPTED' " +
            "and p.time<current_timestamp and p.title like %:searchQuery%")
    Page<Post> postsSearch(Pageable pageable , @Param("searchQuery") String searchQuery);

    @Query(value = "select p from Post p where p.isActive = 1 and p.status = 'ACCEPTED' and" +
            " p.time> :from and p.time< :to")
    List<Post> findPostsByYear(@Param("from") Date from , @Param("to") Date to);

    @Query(value = "select p.time from Post p where p.isActive = 1 and p.status = 'ACCEPTED' and p.time<current_timestamp")
    List<Date> findAllPublicationDate();

    @Query(value = "select p from Post p where p.isActive = 1 and p.status = 'ACCEPTED' and p.time<current_timestamp and " +
            "p.time> :from and p.time< :to")
    Page<Post> findByDate(Pageable pageable,Date from, Date to);

    @Query(value = "select p from Post p join p.tags t join t.tag k where k.name= :tag " +
            "and p.isActive = 1 and p.status = 'ACCEPTED' and p.time<current_timestamp")
    Page<Post> findByTag(Pageable pageable, @Param("tag") String tag);

    @Query(value = "select p from Post p where p.isActive = 1 and p.status = 'ACCEPTED' and p.time<current_timestamp " +
            "and p.id = :id")
    Optional<Post> findById(@Param("id") int id);

}
