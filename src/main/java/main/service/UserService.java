package main.service;

import lombok.SneakyThrows;
import main.api.request.ChangePassRequest;
import main.api.request.EditProfileRequest;
import main.api.request.RegisterRequest;
import main.api.request.RestoreRequest;
import main.api.response.*;
import main.api.response.error.ErrorsPassword;
import main.api.response.error.ErrorsProfile;
import main.api.response.error.ErrorsRegisterBody;
import main.model.Post;
import main.model.User;
import main.repository.CaptchaRepository;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class UserService {

    @Value("${blog.maxFileSize}")
    private int maxFileSize;

    private final UserRepository userRepository;
    private final CaptchaRepository captchaRepository;
    private final EmailService emailService;
    private final StorageService storageService;

    @Autowired
    public UserService(UserRepository userRepository, CaptchaRepository captchaRepository, EmailService emailService, StorageService storageService) {
        this.userRepository = userRepository;
        this.captchaRepository = captchaRepository;
        this.emailService = emailService;
        this.storageService = storageService;
    }

    /**
     * Метод создаёт пользователя в базе данных, если введённые данные верны. Если данные неверные -
     * пользователь не создаётся, а метод возвращает соответствующую ошибку.
     * @param registerRequest
     * @return
     */
    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setResult(true);
        ErrorsRegisterBody errorBody = new ErrorsRegisterBody();
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

    /**
     * Метод обрабатывает информацию, введённую пользователем в форму редактирования своего
     * профиля.
     * @param photo
     * @param editProfileRequest
     * @param principal
     * @return
     */
    @SneakyThrows
    public EditProfileResponse editProfile(MultipartFile photo, EditProfileRequest editProfileRequest, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
        EditProfileResponse edit = new EditProfileResponse();
        edit.setResult(true);
        ErrorsProfile error = new ErrorsProfile();
        if (editProfileRequest.getEmail() != null) {
            if (editProfileRequest.getEmail().equals(userRepository.findByEmail(editProfileRequest.getEmail()))) {
                edit.setResult(false);
                error.setEmail("Этот e-mail уже зарегистрирован");
            }
        }
        if (photo != null) {
            if (photo.getSize() > maxFileSize) {
                edit.setResult(false);
                error.setPhoto("Фото слишком большое, нужно не более 5 Мб");
            }
        }
        if (editProfileRequest.getName() != null) {
            if (!(checkValidName(editProfileRequest.getName()))) {
                edit.setResult(false);
                error.setName("Имя введено неверно");
            }
        }
        if (editProfileRequest.getPassword() != null) {
            if (!(checkValidPassword(editProfileRequest.getPassword()))) {
                edit.setResult(false);
                error.setPassword("Пароль короче 6-ти символов");
            }
        }
        if (edit.isResult()) {
            if (editProfileRequest.getName() != null) {
                user.setName(editProfileRequest.getName());
            }
            if (editProfileRequest.getEmail() != null) {
                user.setEmail(editProfileRequest.getEmail());
            }
            if (editProfileRequest.getPassword() != null) {
                user.setPassword(new BCryptPasswordEncoder(12).encode(editProfileRequest.getPassword()));
            }
            if (photo != null) {
                user.setPhoto(storageService.uploadPhoto(photo, principal));
            }
            if (editProfileRequest.getRemovePhoto() == 1) {
                user.setPhoto(null);
            }
            userRepository.save(user);
        } else {
            edit.setErrors(error);
        }
        return edit;
    }

    public EditProfileResponse editProfileWithoutPhoto(EditProfileRequest editProfileRequest, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
        EditProfileResponse edit = new EditProfileResponse();
        edit.setResult(true);
        ErrorsProfile error = new ErrorsProfile();
        if (editProfileRequest.getEmail() != null) {
            if (editProfileRequest.getEmail().equals(userRepository.findByEmail(editProfileRequest.getEmail()))) {
                edit.setResult(false);
                error.setEmail("Этот e-mail уже зарегистрирован");
            }
        }
        if (editProfileRequest.getName() != null) {
            if (!(checkValidName(editProfileRequest.getName()))) {
                edit.setResult(false);
                error.setName("Имя введено неверно");
            }
        }
        if (editProfileRequest.getPassword() != null) {
            if (!(checkValidPassword(editProfileRequest.getPassword()))) {
                edit.setResult(false);
                error.setPassword("Пароль короче 6-ти символов");
            }
        }
        if (edit.isResult()) {
            if (editProfileRequest.getName() != null) {
                user.setName(editProfileRequest.getName());
            }
            if (editProfileRequest.getEmail() != null) {
                user.setEmail(editProfileRequest.getEmail());
            }
            if (editProfileRequest.getPassword() != null) {
                user.setPassword(new BCryptPasswordEncoder(12).encode(editProfileRequest.getPassword()));
            }
            if (editProfileRequest.getRemovePhoto() == 1) {
                user.setPhoto(null);
            }
            userRepository.save(user);
        } else {
            edit.setErrors(error);
        }
        return edit;
    }

    /**
     * Метод возвращает статистику постов текущего авторизованного пользователя: общие количества
     * параметров для всех публикаций, у который он является автором и доступные для чтения.
     * @param principal
     * @return
     */
    public StatisticResponse getStatistic(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
        StatisticResponse statistic = new StatisticResponse();
        statistic.setPostsCount(user.getPosts().size());
        statistic.setLikesCount(user.getPosts().stream().mapToInt(post -> post.getLikes().size()).sum());
        statistic.setDislikesCount(user.getPosts().stream().mapToInt(post -> post.getDislikes().size()).sum());
        statistic.setViewsCount(user.getPosts().stream().mapToInt(Post::getViewCount).sum());
        statistic.setFirstPublication(user.getPosts().get(0).getTime().getTime() / 1000);
        return statistic;
    }

    /**
     * Метод проверяет наличие в базе пользователя с указанным e-mail. Если пользователь найден, ему
     * должно отправляться письмо со ссылкой на восстановление пароля
     * @param request
     * @param servletRequest
     * @return
     */
    public RestoreResponse restorePass(RestoreRequest request, HttpServletRequest servletRequest) {
        RestoreResponse response = new RestoreResponse();
        response.setResult(true);
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);
        if (user == null) {
            response.setResult(false);
        }
        if (response.isResult()) {
            String code = UUID.randomUUID().toString();
            user.setCode(code);
            userRepository.save(user);

            StringBuilder message = new StringBuilder();
            message.append("Добрый день, \n")
                    .append("Ссылка для восстановления пароля: \n")
                    .append("http://")
                    .append(servletRequest.getHeader("HOST"))
                    .append("/login/change-password/")
                    .append(code);

            emailService.sendSimpleEmail("vladStatsenko1@gmail.com", user.getEmail(), "Password restore", message.toString());
        }
        return response;

    }

    /**
     * Метод проверяет корректность кода восстановления пароля (параметр code) и корректность кодов
     * капчи: введённый код (параметр captcha) должен совпадать со значением в поле code.
     * @param request
     * @return
     */
    public ChangePassResponse changePassword(ChangePassRequest request) {
        ChangePassResponse response = new ChangePassResponse();
        ErrorsPassword error = new ErrorsPassword();

        response.setResult(true);

        User user = userRepository.findByCode(request.getCode());

        if (user == null) {
            response.setResult(false);
            error.setCode("Ссылка для восстановления пароля устарела. " +
                    "<a href=\"/login/restore-password/\">Запросить ссылку снова</a>\"");
        }
        if (!checkValidPassword(request.getPassword())) {
            response.setResult(false);
            error.setPassword("Пароль короче 6-ти символов");
        }
        if (!captchaRepository.findByCode(request.getCaptcha().toUpperCase()
                , request.getCaptchaSecret())) {
            response.setResult(false);
            error.setCaptcha("Код с картинки введен неверно");
        }
        if (response.isResult()) {
            user.setPassword(passwordEncoder().encode(request.getPassword()));
            user.setCode(null);
            userRepository.save(user);
        } else {
            response.setErrors(error);
        }
        return response;
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
