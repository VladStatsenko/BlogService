package main.service;

import main.api.request.SettingsRequest;
import main.api.response.SettingsResponse;
import main.model.User;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class SettingsService {

    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public SettingsService(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Метод возвращает глобальные настройки блога из таблицы global_settings.
     * @return
     */
    public SettingsResponse getGlobalSettings(){
        SettingsResponse settingsResponse = new SettingsResponse();
        settingsResponse.setMultiuserMode(true);
        settingsResponse.setStatisticIsPublic(true);
        return settingsResponse;
    }

    /**
     * Метод записывает глобальные настройки блога в таблицу global_settings, если запрашивающий
     * пользователь авторизован и является модератором.
     * @param settingsRequest
     * @param principal
     * @return
     */
    public SettingsResponse putSettings(SettingsRequest settingsRequest, Principal principal){
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
        if (user==null || user.getIsModerator()!=1){
            return null;
        }
        SettingsResponse response = new SettingsResponse();
        if (settingsRequest.isMultiuserMode()){
            response.setMultiuserMode(true);
        }
        if (settingsRequest.isPostPremoderation()){
            response.setPostPremoderation(true);
        }
        if (settingsRequest.isStatisticIsPublic()){
            response.setStatisticIsPublic(true);
        }
        return response;
    }
}
