package et.com.sample.Controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import et.com.sample.Dto.RequestDto.RegistrationRequest;
import et.com.sample.Dto.ResponseDto.CountResponseDto;
import et.com.sample.Dto.ResponseDto.ResponseDto;
import et.com.sample.Model.User;
import et.com.sample.Repository.UserRepository;
import et.com.sample.Security.Constants;
import et.com.sample.Service.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;

    public AuthController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    private String getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken))
            return authentication.getName();
        return "anonymous user";
    }


    @PostMapping("/login")
    public void login(@RequestParam("phoneNo") String phoneNo, @RequestParam("loginCode") String loginCode) {
        System.out.println("LoggedIn user -> Username {}" + phoneNo + loginCode);
    }

    @PostMapping("/logout")
    public void logout(@RequestParam("userId") long userId) {
//        User user = userRepository.findById(userId).get();
//        user.setUserStatus("logged out");
//        userRepository.save(user);
        System.out.println("User logged out");
    }

    @GetMapping("/refreshtoken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        //Todo: Abstract all JWT related logic to it's own module
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring(7);
                Algorithm algorithm = Algorithm.HMAC256(Constants.TOKEN_SIGNATURE.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);

                String username = decodedJWT.getSubject();
                User user = userService.getUser(username);

                String accessToken = JWT.create()
                        .withSubject(user.getPhone())
                        .withExpiresAt(new Date(System.currentTimeMillis() + Constants.ACCESS_TOKEN_EXP))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRole())
                        .sign(algorithm);

                response.setHeader("access_token", accessToken);
                response.setHeader("refresh_token", refresh_token);

//                new ObjectMapper().writeValue(response.getOutputStream(),
//                        new AuthResponseDto(accessToken, refresh_token));

            } catch (Exception e) {
                System.out.println("Error : logging in: {}" + e.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setHeader("error", e.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }

        } else {
            throw new RuntimeException("Refresh token invalid!");
        }

    }


    @PostMapping("/addBackOfficeUser")
    public ResponseDto RegistrationRequest(@RequestBody RegistrationRequest registrationRequest) {
        return userService.addBackOfficeUser(registrationRequest);
    }


    @PutMapping("/activate_deactivate")
    public ResponseDto activate_deactivate(@RequestParam long userId) {
        return userService.activate_deactivate(userId);
    }

    @PutMapping("/deletePersonalAccount")
    public ResponseDto deletePersonalAccount(@RequestParam long userId) {
        return userService.deletePersonalAccount(userId);
    }

    @PutMapping("/changePassword")
    public ResponseDto changePassword(@RequestParam String oldPassword,
                                      @RequestParam String newPassword,
                                      @RequestParam String phone) {

        User user = userRepository.findByPhone(phone);
        return userService.changePassword(oldPassword, newPassword, user);
    }


    @PostMapping("/requestLostPasswordChange")
    public ResponseDto requestLostPasswordChange(@RequestParam String phoneNo) throws Exception {
        return userService.requestLostPasswordChange(phoneNo);
    }


    @PostMapping("/passwordResettingCode")
    public CountResponseDto<User> passwordResettingCode(@RequestParam String phoneNo,
                                                        @RequestParam int activationCode) throws Exception {
        return userService.passwordResettingCode(phoneNo, activationCode);
    }

    @PostMapping("/resetPassword")
    public ResponseDto resetPassword(@RequestParam String newPassword,
                                     @RequestParam long userId) throws Exception {
        return userService.resetPassword(newPassword, userId);
    }






}
