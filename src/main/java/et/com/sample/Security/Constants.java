package et.com.sample.Security;

public class Constants {

    public static final String[] AUTH_WHITELIST = {
            // -- er UI v2
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**"
            // other public endpoints of your API may be appended to this array
    };

    //JWT
    public static final String LABEL_ACCESS_TOKEN = "JWT";
    public static final String TOKEN_SIGNATURE = "5817b83286e91337b65c7a35d05fd81f4a4e0314";
    public static final int ACCESS_TOKEN_EXP = 2 * 60 * 60 * 1000;
    public static final int REFRESH_TOKEN_EXP = 5 * 60 * 60 * 1000;


}
