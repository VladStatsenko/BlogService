package main.repository;

import main.model.Captcha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptchaRepository extends JpaRepository<Captcha,Integer> {

    @Query(value = "SELECT upper(:code)=c.code " +
            "FROM Captcha c WHERE :secret=c.secretCode")
    boolean findByCode(@Param("code") String code , @Param("secret") String secret);

    @Query(value = "DELETE FROM Captcha c WHERE c.time<NOW() -:interval * INTERVAL '1' hour",nativeQuery = true)
    Captcha dropCaptcha(@Param("interval") int hour);
}
