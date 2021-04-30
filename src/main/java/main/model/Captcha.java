package main.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@Data
@Entity
@Table(name="captcha_codes")
public class Captcha implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Date time;
    private String code;
    @Column(name = "secret_code")
    private String secretCode;


}
