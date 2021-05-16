package main.repository;

import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Integer> {

    @Query(value = "select p from Post p where p.isActive = 1")
    Page<Post> findAllPosts(Pageable pageable);

    @Query(value = "select distinct p from PostVoters v join v.post p " +
            "group by p order by count (v) desc")
    Page<Post> findByLikes(Pageable pageable);

    @Query(value = "select distinct p from PostComment c join c.post p " +
            "group by p order by count (c) desc")
    Page<Post> findByComments(Pageable pageable);

}
