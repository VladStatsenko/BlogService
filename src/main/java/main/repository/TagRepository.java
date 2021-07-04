package main.repository;

import main.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends CrudRepository<Tag, Integer> {

    @Query(value = "SELECT p.tags FROM Post p")
    List<Tag> findFreq();

    @Query(value = "SELECT DISTINCT t FROM Tag t")
    List<Tag> findAll();

   List<Tag> findByName(String name);

    Optional<Tag> findFirstByNameLike(String name);



}
