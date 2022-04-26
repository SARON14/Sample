package et.com.sample.Service;

import et.com.sample.Dto.RequestDto.RegistrationRequest;
import et.com.sample.Dto.ResponseDto.CountResponseDto;
import et.com.sample.Dto.ResponseDto.ResponseDto;
import et.com.sample.Model.User;
import et.com.sample.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

@Service
@Transactional
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUser(String phone) {
        System.out.println("Fetching user {} " + phone);
        return userRepository.findByPhone(phone);
    }

    public ResponseDto addBackOfficeUser(RegistrationRequest registrationRequest) {
        User existingUser = userRepository.findByPhone(registrationRequest.getUserName());
        if (existingUser != null) {
            return new ResponseDto(false, "User name already exist");
        } else {
            User user = new User();
            user.setPassword(bCryptPasswordEncoder.encode(registrationRequest.getPassword()));
            user.setUserStatus("active");
            user.setRole("admin");
            user.setPhone(registrationRequest.getPhone());
            user.setEmail(registrationRequest.getEmail());
            user.setUserName(registrationRequest.getUserName());
            user.setDob(registrationRequest.getBirthDate());
            user.setGender(registrationRequest.getGender());
            user.setName(registrationRequest.getFullName());
            user.setCreatedOn(Instant.now());
            userRepository.save(user);

            return new ResponseDto<>(true, "created Successfully");
        }
    }

    public ResponseDto activate_deactivate(long userId) {
        User user = userRepository.findById(userId).get();
        if (user.getUserStatus().equals("active")) {
            user.setUserStatus("deactivated");
        }
        else
        if (user.getUserStatus().equals("deactivated")) {
            user.setUserStatus("active");
        }

        userRepository.save(user);
        return new ResponseDto(true, "success");
    }

    public ResponseDto deletePersonalAccount(Long userId){
        User user = userRepository.findById(userId).get();
        if (user.getRole().equals("admin")) {
            userRepository.delete(user);

        } else {
            user.setUserStatus("deleted");
            user.setAccountDeletedOn(Instant.now());
            userRepository.save(user);
        }

        return new ResponseDto(true, "success");
    }

    public ResponseDto changePassword(String oldPassword, String newPassword, User user) {
        try {
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return new ResponseDto(false, "please review your old password!");
            } else {
                String hashPassword = this.passwordEncoder.encode(newPassword);
                user.setPassword(hashPassword);
                userRepository.save(user);
                return new ResponseDto(true, "password changed Successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, "Unable to change Password. due to:- " + e.getMessage());
        }
    }

    public ResponseDto requestLostPasswordChange(String phoneNo) throws Exception {
        User user = userRepository.findByPhone(phoneNo);

        int code = generateRandomNumber(6);
        String phone =user.getPhone();
        if (user.getUserStatus().equals("active")) {
            user.setVerificationCode(code);
            user.setVerificationCodeStatus("active");
            user.setUserStatus("pending");
            userRepository.save(user);
            //           Todo 2 sms sender service will be enabled once the service availability is confirmed
//                     smsService.sendSMS(" Welcome to Gomlala. "+ code+ "  is your password resetting code : ", smsService.cleanPhone(phone));
//            if(userActualDatum.getEmail() != null) {
//                emailSenderService.sendSimpleEmail(userActualDatum.getEmail(),
//                        "this is the password resetting code" + code,
//                        "Email Verification Code:" + code);
//            }
            return new ResponseDto(true, String.valueOf(code));
        } else {
            return new ResponseDto(false, "User registered with this phone no is no longer active user!");
        }
    }

    public CountResponseDto<User> passwordResettingCode(String phoneNo, int activationCode) {
        User user = userRepository.findByPhone(phoneNo);
        if (user.getVerificationCodeStatus().equals("used") || user.getVerificationCode() != activationCode) {
            ResponseDto responseDto = new ResponseDto();
            responseDto.setStatus(false);
            responseDto.setMsg("Invalid code!");
            return new CountResponseDto<>(null, 0, responseDto);
        } else {
            user.setVerificationCodeStatus("used");
            user.setVerificationCodeUsedOn(Instant.now());
            userRepository.save(user);
            return new CountResponseDto<>(user, 1, new ResponseDto<>(true, "success"));
        }
    }

    public ResponseDto resetPassword(String newPassword, long userId) {
        try {
            User user = userRepository.findById(userId).get();
            if (user.getVerificationCode() != 0 && user.getVerificationCodeStatus().equals("used")) {
                String hashPassword = this.passwordEncoder.encode(newPassword);
                user.setPassword(hashPassword);
                user.setUserStatus("active");
                userRepository.save(user);
                return new ResponseDto(true, "Password reset Successfully");
            } else return new ResponseDto(false, "Unable to reset password");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDto(false, "Unable to change Password. due to:- " + e.getMessage());
        }

    }
    public int generateRandomNumber(int degit) {
        Random rand = new Random();
        int activationCode = rand.nextInt(1000000);
        while (String.valueOf(activationCode).length() < degit) {
            activationCode = rand.nextInt(1000000);
        }
        return activationCode;
    }

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        User user = userRepository.findByPhone(phone);
        if (user == null) {
            throw new UsernameNotFoundException("User not found in db");
        } else {
            System.out.println("phone no found in db" + phone);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        return new org.springframework.security.core.userdetails.User(user.getPhone(),
                user.getPassword(), authorities);
    }
}
