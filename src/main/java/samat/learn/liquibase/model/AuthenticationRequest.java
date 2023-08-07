package samat.learn.liquibase.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Запрос авторизации", value = "value")
public class AuthenticationRequest {
    @ApiModelProperty(value = "email пользователя", example = "JohnDoe@gmail.com")
    private String email;
    private String password;
    private String fingerprint;
}