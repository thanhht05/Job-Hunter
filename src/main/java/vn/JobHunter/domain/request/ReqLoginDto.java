package vn.JobHunter.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqLoginDto {
    @NotBlank(message = "Username không được trống")
    private String username;
    @NotBlank(message = "Password không được trống")
    private String password;

}
