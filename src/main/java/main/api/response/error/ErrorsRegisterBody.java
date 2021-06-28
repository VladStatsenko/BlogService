package main.api.response.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorsRegisterBody {
    private String email;
    private String name;
    private String password;
    private String captcha;
}
