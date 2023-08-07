package samat.learn.liquibase.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import samat.learn.liquibase.entity.User;
import samat.learn.liquibase.mapper.UserMapper;
import samat.learn.liquibase.model.user.UserResponse;
import samat.learn.liquibase.repository.UserRepository;
import samat.learn.liquibase.service.impl.UserService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void findAll(){
        List<User> users = Mockito.mock(List.class);

        when(userRepository.findAll()).thenReturn(users);

        List<UserResponse> findUsers = userService.findAll();

        Assertions.assertThat(findUsers).isNotNull();
    }
}