package samat.learn.liquibase.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import samat.learn.liquibase.entity.Role;
import samat.learn.liquibase.model.role.RoleResponse;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleResponse map(Role role);
}
