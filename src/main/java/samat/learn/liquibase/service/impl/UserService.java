package samat.learn.liquibase.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import samat.learn.liquibase.entity.User;
import samat.learn.liquibase.mapper.UserMapper;
import samat.learn.liquibase.model.user.UserResponse;
import samat.learn.liquibase.repository.UserRepository;
import samat.learn.liquibase.specification.UserSpecification;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponse> findAll() {
        var users = userRepository.findAll();
        return users.stream()
                .map(p -> this.userMapper.map(p))
                .collect(Collectors.toList());
    }
}
