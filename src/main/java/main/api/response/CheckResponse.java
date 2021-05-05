package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.api.response.body.UserBody;
import main.model.User;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckResponse {
    private boolean result;
    private UserBody user;

}
