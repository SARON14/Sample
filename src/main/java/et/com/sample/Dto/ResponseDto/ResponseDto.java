package et.com.sample.Dto.ResponseDto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseDto<T> {
    private boolean status;
    private String msg;

//    private T model;
}
