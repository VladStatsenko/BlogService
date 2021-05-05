package main.api.response.body;

import lombok.Data;
import main.model.User;

@Data
public class UserBody {
    private User id;
    private User name;
    private User photo;
    private User email;
    private boolean moderation;
    private int moderationCount;
    private boolean settings;
}
