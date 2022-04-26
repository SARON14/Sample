package et.com.sample.Config;

import et.com.sample.Security.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;
import java.util.List;
import java.util.Properties;


@Configuration
public class Config {
    public static final String AUTHORIZATION_HEADER = "Authorization";

    private ApiInfo apiInfo() {
        return new ApiInfo("User Manager API",
                "Let's a company manage users.",
                "1.0",
                "Terms of service",
                new Contact("Act Technologies", "www.sample.com.et", "saront@act.com.et"),
                "License of API",
                "API license URL",
                Collections.emptyList());
    }

    @Bean
    public Docket swaggerConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .securityContexts(Collections.singletonList(securityContext()))
                .securitySchemes(Collections.singletonList(apiKey()))
                .select()
                .paths(PathSelectors.ant("/**"))
                .apis(RequestHandlerSelectors.basePackage("et.com.sample"))
                .build();
    }


    private ApiKey apiKey() {
        return new ApiKey(Constants.LABEL_ACCESS_TOKEN, AUTHORIZATION_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(new SecurityReference("JWT", authorizationScopes));
    }

    @Bean
    public JavaMailSender javaMailSender() {

        JavaMailSenderImpl ja = new JavaMailSenderImpl();
        ja.setHost("smtp.gmail.com");
        ja.setPort(25);
        ja.setProtocol("smtp");

        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.tls.enable", true);
        props.put("mail.smtp.timeout", 25000);
        props.put("mail.smtp.starttls.enable", true);
        ja.setJavaMailProperties(props);
        ja.setUsername("sharonabi14@gmail.com");
        ja.setPassword("Enatefiker14");
        return ja;
    }
}
