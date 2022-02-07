package service;


import kma.topic2.junit.model.NewUser;
import kma.topic2.junit.model.User;
import kma.topic2.junit.repository.UserRepository;
import kma.topic2.junit.service.UserService;
import kma.topic2.junit.validation.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @SpyBean
    private UserValidator userValidator;

    private NewUser.NewUserBuilder userBuilder;

    @BeforeEach
    void setUp() {
        userBuilder = NewUser.builder()
                .fullName("Test User")
                .password("qwerty");
    }

    @Test
    @DisplayName("Creates new user and adds it to repository")
    void createsUser() {
        var user = userBuilder.login("login").build();
        userService.createNewUser(user);
        assertThat(userService.getUserByLogin(user.getLogin()))
                .returns(user.getLogin(), User::getLogin)
                .returns(user.getPassword(), User::getPassword);
    }

    @Test
    @DisplayName("Calls validateNewUser when creating user")
    void validatesNewUser() {
        var user = userBuilder.login("newlogin").build();
        userService.createNewUser(user);
        verify(userValidator).validateNewUser(user);
    }
}
