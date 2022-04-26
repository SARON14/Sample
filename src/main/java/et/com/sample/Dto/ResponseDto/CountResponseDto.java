package et.com.sample.Dto.ResponseDto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CountResponseDto<T> {
    private T model;
    private long count;
    private ResponseDto responseDto;
}
