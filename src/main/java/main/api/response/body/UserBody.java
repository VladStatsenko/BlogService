package main.api.response.body;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.model.User;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserBody {
    private int id;
    private String name;
    private String photo;
    private String email;
    private boolean moderation;
    private int moderationCount;
    private boolean settings;
}
