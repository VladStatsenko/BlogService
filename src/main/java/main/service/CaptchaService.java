package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import main.api.response.CaptchaResponse;
import main.model.Captcha;
import main.repository.CaptchaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

@Service
public class CaptchaService {

    private final String DATA_TYPE_PREFIX = "data:image/png;base64, ";
    private final CaptchaRepository captchaRepository;

    @Autowired
    public CaptchaService(CaptchaRepository captchaRepository) {
        this.captchaRepository = captchaRepository;
    }

    /**
     * Метод генерирует коды капчи, - отображаемый и секретный, - сохраняет их в базу данных
     * @return
     */
    public CaptchaResponse getCaptcha() {
        Cage cage = new GCage();
        Captcha captcha = new Captcha();
        CaptchaResponse captchaResponse = new CaptchaResponse();

        String codeOnImage = generateCaptcha(5);
        String secretCode = cage.getTokenGenerator().next();
        String base64Image = Base64.getEncoder().encodeToString(cage.draw(codeOnImage));

        captcha.setCode(codeOnImage);
        captcha.setSecretCode(secretCode);
        captcha.setTime(LocalDateTime.now(ZoneId.systemDefault()));
        captchaRepository.save(captcha);

        captchaResponse.setSecret(secretCode);
        captchaResponse.setImage(DATA_TYPE_PREFIX + base64Image);

        return captchaResponse;
    }

    @Scheduled(fixedDelay = 3600 * 1000)
    public void deleteOld() {
        captchaRepository.dropCaptcha(1);
    }

    private String generateCaptcha(int captchaLength) {

        String captcha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < captchaLength; i++) {
            stringBuilder.append(captcha.charAt((int) (Math.random() * captcha.length())));
        }
        return stringBuilder.toString();

    }
}
