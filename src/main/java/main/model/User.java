package main.model;

import com.sun.source.doctree.SerialDataTree;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@Entity
@Table(name="users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int isModerator;
    private LocalDateTime regTime;
    private String name;
    private String email;
    private String password;
    private String code;
    private String photo;

    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<PostVoters> postVoters = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<PostComment> postComments = new ArrayList<>();

    @OneToMany(mappedBy = "moderator")
    private List<Post> moderatorPosts = new ArrayList<>();

    public Role getRole(){
        return isModerator == 1 ? Role.MODERATOR : Role.USER;
    }
}
