package main.service;

import main.api.request.SettingsRequest;
import main.api.response.SettingsResponse;
import main.model.User;
import main.repository.SettingsRepository;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class SettingsService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;

    @Autowired
    public SettingsService(UserService userService, UserRepository userRepository, SettingsRepository settingsRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.settingsRepository = settingsRepository;
    }

    /**
     * Метод возвращает глобальные настройки блога из таблицы global_settings.
     *
     * @return
     */
    public SettingsResponse getGlobalSettings() {
        SettingsResponse settingsResponse = new SettingsResponse();
        settingsResponse.setMultiuserMode(settingsRepository.findAllGlobalSettings("MULTIUSER_MODE").getValue().equals("YES"));
        settingsResponse.setPostPremoderation(settingsRepository.findAllGlobalSettings("POST_PREMODERATION").getValue().equals("YES"));
        settingsResponse.setStatisticIsPublic(settingsRepository.findAllGlobalSettings("STATISTICS_IS_PUBLIC").getValue().equals("YES"));
        return settingsResponse;
    }

    /**
     * Метод записывает глобальные настройки блога в таблицу global_settings, если запрашивающий
     * пользователь авторизован и является модератором.
     *
     * @param settingsRequest
     * @param principal
     * @return
     */
    public Boolean putSettings(SettingsRequest settingsRequest, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
        if (user == null || user.getIsModerator() != 1) {
            return null;
        }
        String MULTIUSER_MODE = settingsRequest.isMultiuserMode() ? "YES" : "NO";
        String POST_PREMODERATION = settingsRequest.isPostPremoderation() ? "YES" : "NO";
        String STATISTICS_IS_PUBLIC = settingsRequest.isStatisticIsPublic() ? "YES" : "NO";

        settingsRepository.insertSettings("MULTIUSER_MODE", MULTIUSER_MODE);
        settingsRepository.insertSettings("POST_PREMODERATION", POST_PREMODERATION);
        settingsRepository.insertSettings("STATISTICS_IS_PUBLIC", STATISTICS_IS_PUBLIC);
        return true;
    }
}
