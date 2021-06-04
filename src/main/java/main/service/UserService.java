package main.service;

import main.api.request.body.RegisterRequest;
import main.api.response.RegisterResponse;
import main.api.response.body.ErrorBody;
import main.model.User;
import main.repository.CaptchaRepository;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CaptchaRepository captchaRepository;

    @Autowired
    public UserService(UserRepository userRepository, CaptchaRepository captchaRepository) {
        this.userRepository = userRepository;
        this.captchaRepository = captchaRepository;
    }

    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setResult(true);
        ErrorBody errorBody = new ErrorBody();
        if (userRepository.existsByEmail(registerRequest.getEmail().toLowerCase())) {
            registerResponse.setResult(false);
            errorBody.setEmail("этот e-mail уже зарегистрирован");
        }
        if (!checkValidName(registerRequest.getName())) {
            registerResponse.setResult(false);
            errorBody.setName("Имя указано неверно");
        }
        if (!checkValidPassword(registerRequest.getPassword())) {
            registerResponse.setResult(false);
            errorBody.setPassword("Пароль короче 6-ти символов");
        }
        if (!captchaRepository.findByCode(registerRequest.getCaptcha().toUpperCase()
                , registerRequest.getCaptchaSecret())) {
            registerResponse.setResult(false);
            errorBody.setCaptcha("Код с картинки введен неверно");
        }
        if (registerResponse.isResult()) {
            User user = new User();
            user.setName(registerRequest.getName());
            user.setEmail(registerRequest.getEmail().toLowerCase());
            user.setRegTime(LocalDateTime.now(ZoneId.systemDefault()));
            user.setPassword(passwordEncoder().encode(registerRequest.getPassword()));
            user.setIsModerator(0);
            userRepository.save(user);
        } else {
            registerResponse.setErrors(errorBody);
        }
        return registerResponse;
    }

    private Boolean checkValidName(String username) {
        return username != null
                && username.length() >= 1
                && username.length() <= 80
                && username.matches("[A-ZА-Яa-zа-я][A-ZА-Яa-zа-я]*");
    }

    private Boolean checkValidPassword(String password) {
        return password != null
                && password.length() <= 6;
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }


}
