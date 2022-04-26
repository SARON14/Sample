package et.com.sample.Security;


import org.springframework.beans.factory.annotation.Value;

/**
 * Service which provides operations for authentication tokens.
 *
 */
public class AuthenticationTokenService {

    @Value("${ authentication.jwt.validFor }")
    private Long validFor;

    @Value("${ authentication.jwt.refreshLimit }")
    private Integer refreshLimit;
}
