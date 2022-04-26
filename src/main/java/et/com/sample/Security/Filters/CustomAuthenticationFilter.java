package et.com.sample.Security.Filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import et.com.sample.Dto.ResponseDto.AuthResponseDto;
import et.com.sample.Repository.UserRepository;
import et.com.sample.Security.Constants;
import et.com.sample.Service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


import static et.com.sample.Security.Constants.TOKEN_SIGNATURE;


public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private UserService userService;


    //This should come from AppSecurityConfig
    private final AuthenticationManager authenticationManager;

    private UserRepository userRepository;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, ApplicationContext ctx) {
        this.authenticationManager = authenticationManager;
        this.userRepository= ctx.getBean(UserRepository.class);
        this.userService= ctx.getBean(UserService.class);
//        this.userService=ctx.getBean(UserService.class);
        //        this.userActualDataRepository=ctx.getBean();

    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String phoneNO = request.getParameter("phoneNo");
        String loginCode = request.getParameter("loginCode");
        System.out.println("attempted to login " + phoneNO);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(phoneNO, loginCode);

        return authenticationManager.authenticate(authenticationToken);
    }

    public static boolean loginStatus = false;
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication)
            throws IOException, ServletException {

        User user = (User) authentication.getPrincipal();
        System.out.println("phoneNo found "+ user.getUsername());
//        et.com.act.fiker.model.User userModel = userRepository.findByPhone(user.getUsername());
//        userModel.setUserStatus("active");
//        System.out.println("userStatus settled to active ");
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SIGNATURE.getBytes());

        System.out.println("Logged In User " + user.getUsername());
        loginStatus = true;


        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + Constants.ACCESS_TOKEN_EXP))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles",
                        user.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + Constants.REFRESH_TOKEN_EXP))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);


//         response.setHeader("access_token", accessToken);
//         response.setHeader("refresh_token", refreshToken);
//         Map<String,String> tokens = new HashMap<>();
//         tokens.put("access_token", accessToken);
//         tokens.put("refresh_token", refreshToken);
//        et.com.act.fiker.model.User userModel = userRepository.findByPhone(user.getUsername());
//        System.out.println("userId --> "+ userModel.getId());
//        new AuthResponseDto(user.getUsername(),
//                userService.getUser(user.getUsername()).getId(),//loggeduser
////                        getUserModel(user.getUsername()).getId(),
//                user.getAuthorities().toString(),true,
////                        userActualDataRepository.findUserActualDatumByUser(getUserModel(user.getUsername())).getFullName() != null,
//                accessToken,
//                refreshToken,
//                1L,true,true
//        )

        AuthResponseDto authResponseDto=new AuthResponseDto();
        authResponseDto.setLoggedInUser(user.getUsername());
        authResponseDto.setAssignedRoles(user.getAuthorities().toString());
        authResponseDto.setAccess_token(accessToken);
        authResponseDto.setRefresh_token(refreshToken);
        System.out.println("user.getUsername()="+user.getUsername());
et.com.sample.Model.User userdetail = userRepository.findByPhone(user.getUsername());
        if(userdetail.getUserStatus().equalsIgnoreCase("active") || userdetail.getUserStatus().equalsIgnoreCase("pending")){
        }else{
            throw new InternalAuthenticationServiceException("Errorrr", null);
        }

        String fullName=userdetail.getName();


        AuthResponseDto userAuthResponseDetail = getUserAuthResponseDetail(userdetail,fullName, authResponseDto);
        System.out.println("userAuthResponseDetail to sting => "+userAuthResponseDetail.toString());
        //userService.getUserAuthResponseDetail(user.getUsername(),authResponseDto)
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(),   userAuthResponseDetail  );
      }




    public AuthResponseDto getUserAuthResponseDetail(et.com.sample.Model.User user, String fullName,
                                                     AuthResponseDto authResponseDto) throws UsernameNotFoundException {
        System.out.println("Stage0");
        System.out.println("Stage1");
        authResponseDto.setLoggedInUserId(user.getId());

//        List<Picture> pictureByUser = picturePathRepository.findPictureByUser(user);
//        authResponseDto.setIsProfilePictureAdded(false);
//        pictureByUser.forEach(picture -> {
//            if(picture.getIsProfilePicture()){
//                //Yes has.
//                authResponseDto.setIsProfilePictureAdded(true);
//            }
//        });
        System.out.println("Stage2");

//        if(userPassionCount>0){
//            //user has passion list.
//            authResponseDto.setIsUserAddedpassion(true);
//        }else{
//            authResponseDto.setIsUserAddedpassion(false);
//        }
        System.out.println("Stage3");

        if(fullName!=null && !fullName.isEmpty()){
            ////user has fill fullname
            authResponseDto.setIsFirstFormSubmitted(true);
        }else{
            authResponseDto.setIsFirstFormSubmitted(false);

        }

        System.out.println("Stage4");

//                    //authResponseDto.getPaymentSubscriptionId()==null to avoid repetation
//                    userSubscription.setUser(null);
//                    authResponseDto.setPaymentSubscriptionId(userSubscription.getId());
//                    authResponseDto.setUserSubscription(userSubscription);
//                }
//            });
//        }
        if(user.getUserStatus().equalsIgnoreCase("active")){
            authResponseDto.setIsVerified(true);
        }else{
            authResponseDto.setIsVerified(false);
        }


        return authResponseDto;
    }
}