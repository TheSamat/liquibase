package samat.learn.liquibase.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import samat.learn.liquibase.entity.User;
import samat.learn.liquibase.model.user.UserResponse;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(expression = "java(user.getFirstName() + \" \" + user.getLastName())", target = "FIO")
    UserResponse map(User user);
}

