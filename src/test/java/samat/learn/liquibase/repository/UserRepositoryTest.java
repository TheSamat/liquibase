package samat.learn.liquibase.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import samat.learn.liquibase.entity.Role;
import samat.learn.liquibase.entity.User;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private void clearUsers() {
        userRepository.deleteAll();
    }

    @Test
    public void save() {
        //region Arrange
        User user = new User(
                null,
                "firstName",
                "lastName",
                "email@gmail.com",
                "password",
                new Role(1L, null, null)
        );
        //endregion

        //region Act
        User savedUser = userRepository.save(user);
        //endregion

        //region Assert
        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isGreaterThan(0);
        Assertions.assertThat(savedUser.getFirstName()).isEqualTo("firstName");
        Assertions.assertThat(savedUser.getLastName()).isEqualTo("lastName");
        Assertions.assertThat(savedUser.getEmail()).isEqualTo("email@gmail.com");
        Assertions.assertThat(savedUser.getPassword()).isEqualTo("password");

        Assertions.assertThat(savedUser.getRole()).isNotNull();
        Assertions.assertThat(savedUser.getRole().getId()).isEqualTo(1);

        //endregion
    }

    @Test
    public void findAll() {
        //region Arrange
        List<User> users = Arrays
                .asList(
                new User(
                        null,
                        "firstName",
                        "lastName",
                        "email@gmail.com",
                        "password",
                        new Role(1L, null, null)
                ),
                new User(
                        null,
                        "firstName2",
                        "lastName2",
                        "email2@gmail.com",
                        "password2",
                        new Role(1L, null, null)
                )
        );
        //endregion

        //region Act
        clearUsers();
        userRepository.saveAll(users);
        List<User> findUsers = userRepository.findAll();
        //endregion

        //region Assert
        Assertions.assertThat(findUsers).isNotNull();
        Assertions.assertThat(findUsers.size()).isEqualTo(2);
        //endregion
    }

    @Test
    public void findByEmail() {
        //region Arrange
        User user = new User(
                null,
                "firstName",
                "lastName",
                "email@gmail.com",
                "password",
                new Role(1L, null, null)
        );
        //endregion

        //region Act
        userRepository.save(user).getId();
        User findUser = userRepository.findByEmail("email@gmail.com").get();
        //endregion

        //region Assert
        Assertions.assertThat(findUser).isNotNull();
        Assertions.assertThat(findUser.getId()).isGreaterThan(0);
        Assertions.assertThat(findUser.getEmail()).isEqualTo("email@gmail.com");
        Assertions.assertThat(findUser.getFirstName()).isEqualTo("firstName");
        Assertions.assertThat(findUser.getLastName()).isEqualTo("lastName");
        Assertions.assertThat(findUser.getPassword()).isEqualTo("password");
        Assertions.assertThat(findUser.getRole()).isNotNull();
        Assertions.assertThat(findUser.getRole().getId()).isEqualTo(1);
        //endregion
    }

    @Test
    @DisplayName("findByEmail() throw exception when user with this email not exists")
    public void findByEmail_whenUserWithThisEmailNotExists() {
        clearUsers();
        Throwable exception = assertThrows(RuntimeException.class, () -> {
            userRepository.findByEmail("email@gmail.com")
                    .orElseThrow(() -> new RuntimeException());
        });
    }

    @Test
    public void findById() {
        //region Arrange
        User user = new User(
                null,
                "firstName",
                "lastName",
                "email@gmail.com",
                "password",
                new Role(1L, null, null)
        );
        //endregion

        //region Act
        Long id = userRepository.save(user).getId();
        User findUser = userRepository.findById(id).get();
        //endregion

        //region Assert
        Assertions.assertThat(findUser).isNotNull();
        Assertions.assertThat(findUser.getId()).isEqualTo(id);
        Assertions.assertThat(findUser.getFirstName()).isEqualTo("firstName");
        Assertions.assertThat(findUser.getLastName()).isEqualTo("lastName");
        Assertions.assertThat(findUser.getEmail()).isEqualTo("email@gmail.com");
        Assertions.assertThat(findUser.getPassword()).isEqualTo("password");
        Assertions.assertThat(findUser.getRole()).isNotNull();
        Assertions.assertThat(findUser.getRole().getId()).isEqualTo(1);
        //endregion
    }
}