package et.com.sample.Security;


import et.com.sample.Security.Filters.CustomAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AppSecurityConfig(UserDetailsService userDetailService,
                             BCryptPasswordEncoder passwordEncoder) {
        this.userDetailService = userDetailService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //Gives ss the service used for looking up users from db and the password encoder
        //for hashing passwords before comparison and validation.
        auth.userDetailsService(userDetailService).passwordEncoder(passwordEncoder);

    }

    /***
     * author : saron
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //To change the built in base /login url with custom url
        //This can also be done withing application.properties
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean(),getApplicationContext());
        customAuthenticationFilter.setFilterProcessesUrl("/auth/login");

        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/login/**", "/logout/**", "auth/refreshtoken/**").permitAll();
        http.authorizeRequests().antMatchers(Constants.AUTH_WHITELIST).permitAll();
        http.authorizeRequests().antMatchers("/users/*").permitAll();
//        http.authorizeRequests().antMatchers("/backOffice/addPaymentType").hasRole(Role.admin.name());;
        http.authorizeRequests().antMatchers("/auth/*").permitAll();;
//        http.authorizeRequests().antMatchers(POST, "/user/**").hasAuthority("user");
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(customAuthenticationFilter);
        //http.addFilter(new CustomAuthenticationFilter(authenticationManagerBean()));
        //custom filter/middleware for token authentication
        //This will be placed first in the filter chain i.e all requests will go through it before any other filter
        http.addFilterBefore(new CustomAuthenticationFilter(authenticationManager(),getApplicationContext()), UsernamePasswordAuthenticationFilter.class);
        http.cors();
        http.headers().frameOptions().sameOrigin(); // to show my database
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
