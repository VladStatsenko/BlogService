package main.repository;

import main.model.Post;
import main.model.PostVoters;
import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<PostVoters,Integer> {
    PostVoters findVoteByUserAndPost(User user, Post post);
}
