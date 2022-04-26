package et.com.sample.Model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="\"user\"")
@Entity
public class User extends Model {

    private String name;
    private String userStatus;
    private Instant dob;
    private Integer age;
    private String role;
    private String gender;
    private String phone;
    private String email;
    private String userName;
    private String password;
    private Instant accountDeletedOn;
    private Integer verificationCode;
    private String verificationCodeStatus;
    private Instant verificationCodeUsedOn;
}
