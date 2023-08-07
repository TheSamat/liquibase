package samat.learn.liquibase.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import samat.learn.liquibase.entity.Role;
import samat.learn.liquibase.entity.Session;
import samat.learn.liquibase.model.role.RoleResponse;

import javax.persistence.*;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    Long id;
    String firstName;
    String lastName;
    String email;
    String password;

    RoleResponse role;
    String FIO;
}