package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.api.response.body.UserBody;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private boolean result;
    private UserBody user;
}
