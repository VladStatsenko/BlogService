package main.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="posts")
public class Post implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "is_active")
    private boolean isActive;
    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status")
    private ModerationStatus status;
    @ManyToOne
    @JoinColumn(name = "moderator_id")
    private User moderator;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Date time;
    private String title;
    private String text;
    @Column(name = "view_count")
    private int viewCount;

    public enum ModerationStatus {
    NEW,ACCEPTED,DECLINED
    }

    @OneToMany(mappedBy = "post")
    @LazyCollection(LazyCollectionOption.EXTRA)
    @Where(clause = "value = 1")
    private List<PostVoters> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    @LazyCollection(LazyCollectionOption.EXTRA)
    @Where(clause = "value = -1")
    private List<PostVoters> dislikes = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<TagPost> tags = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostComment> comments = new ArrayList<>();
}
