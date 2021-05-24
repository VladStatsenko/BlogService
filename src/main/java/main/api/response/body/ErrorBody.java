package main.api.response.body;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorBody {
    private String email;
    private String name;
    private String password;
    private String captcha;
}
