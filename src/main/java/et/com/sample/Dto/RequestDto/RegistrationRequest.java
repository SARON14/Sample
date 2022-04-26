package et.com.sample.Dto.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    private String userName;
    private String password;
    private String fullName;
    private Instant birthDate;
    private String phone;
    private String email;
    private String gender;
}
