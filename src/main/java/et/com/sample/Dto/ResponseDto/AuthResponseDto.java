package et.com.sample.Dto.ResponseDto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    private String loggedInUser;
    private Long LoggedInUserId;//
    private String assignedRoles;
    private Boolean isFirstFormSubmitted;//
    private String access_token;
    private String refresh_token;
    private Long paymentSubscriptionId;//
    private Boolean isUserAddedpassion;
    private Boolean isProfilePictureAdded;//
    private Boolean isVerified;


}
