package validation;

import kma.topic2.junit.exceptions.ConstraintViolationException;
import kma.topic2.junit.exceptions.LoginExistsException;
import kma.topic2.junit.model.NewUser;
import kma.topic2.junit.repository.UserRepository;
import kma.topic2.junit.validation.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserValidatorTest {
    private static final String USERNAME = "Test User";
    private static final String USER_LOGIN = "test_user";
    private static final String USER_PASSWORD = "qwerty";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    private NewUser normalUser;

    @BeforeEach
    void setUp() {
        normalUser = NewUser.builder()
                .fullName(USERNAME)
                .login(USER_LOGIN)
                .password(USER_PASSWORD)
                .build();
    }

    @Test
    @DisplayName("Calls isLoginExists when validating user")
    void validatesLoginExistence() {
        userValidator.validateNewUser(normalUser);
        verify(userRepository).isLoginExists(USER_LOGIN);
    }

    @Test
    @DisplayName("Throws LoginExistsException for nonunique login")
    void throwsExceptionForNotUniqueLogin() {
        when(userRepository.isLoginExists(normalUser.getLogin())).thenReturn(true);
        assertThrows(LoginExistsException.class, () -> userValidator.validateNewUser(normalUser));
    }

    @Test
    @DisplayName("Doesn't throw exceptions for good user")
    void doesNotThrowExceptionsForGoodUser() {
        assertDoesNotThrow(() -> userValidator.validateNewUser(normalUser));
    }

    @Test
    @DisplayName("Throws ConstraintViolationException for short password")
    void throwsExceptionForShortPassword() {
        var shortPasswordUser = NewUser.builder()
                .fullName(USERNAME)
                .login(USER_LOGIN)
                .password("1")
                .build();

        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class,
                () -> userValidator.validateNewUser(shortPasswordUser));

        assertThat(ex.getErrors()).containsExactly("Password has invalid size");
    }

    @Test
    @DisplayName("Throws ConstraintViolationException for long password")
    void throwsExceptionForLongPassword() {
        var longPasswordUser = NewUser.builder()
                .fullName(USERNAME)
                .login(USER_LOGIN)
                .password("123456789")
                .build();

        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class,
                () -> userValidator.validateNewUser(longPasswordUser));

        assertThat(ex.getErrors()).containsExactly("Password has invalid size");
    }

    @Test
    @DisplayName("Throws ConstraintViolationException for password with bad symbol")
    void throwsExceptionForPasswordWithBadSymbol() {
        var badPasswordUser = NewUser.builder()
                .fullName(USERNAME)
                .login(USER_LOGIN)
                .password("[123]")
                .build();

        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class,
                () -> userValidator.validateNewUser(badPasswordUser));

        assertThat(ex.getErrors()).containsExactly("Password doesn't match regex");
    }
}
