package main.api.response.body;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserBody {
    private int id;
    private String name;
    private String photo;
    private String email;
    private boolean moderation;
    private int moderationCount;
    private boolean settings;

    public UserBody(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.photo = user.getPhoto();
    }
}
