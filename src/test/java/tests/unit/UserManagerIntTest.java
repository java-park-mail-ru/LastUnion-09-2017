package tests.unit;

import com.github.javafaker.Faker;
import lastunion.application.Application;
import lastunion.application.managers.UserManager;
import lastunion.application.models.SignInModel;
import lastunion.application.models.SignUpModel;
import lastunion.application.models.UserModel;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.NotNull;
import java.util.Locale;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertSame;

@SuppressWarnings( {"DefaultFileTemplate", "RedundantSuppression"})
@SpringBootTest(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Category(tests.IntegrationTest.class)
public class UserManagerIntTest {
    @Autowired
    private UserManager userManager;
    private static Faker faker;
    private static String userEmail;
    private static String userName;
    private static String userPassword;

    @BeforeClass
    public static void setUpFaker() {
        faker = new Faker(new Locale("en-US"));
    }

    public void signUpUserOk(@NotNull String uName, @NotNull String uPassword, @NotNull String uEmail){
        final SignUpModel signUpModel = new SignUpModel(uName, uPassword, uEmail);
        final UserManager.ResponseCode responseCode = userManager.signUpUser(signUpModel);
        assertSame(responseCode, UserManager.ResponseCode.OK);
    }

    @Before
    public void setUp() {
        userEmail = faker.internet().emailAddress();
        userName = faker.name().username();
        userPassword = faker.internet().password();
        signUpUserOk(userName, userPassword, userEmail);
    }

    @Test
    public void checkPasswordByUserNameOk() {
        final boolean result = userManager.checkPasswordByUserName(userPassword, userName);
        assertSame(result, true);
    }

    @SuppressWarnings("InstanceMethodNamingConvention")
    @Test
    public void checkPasswordByUserNameWithIncorrectPassword() {
        final boolean result = userManager.checkPasswordByUserName(faker.internet().password(), userName);
        assertSame(result, false);
    }

    @Test
    public void signInUserOk() {
        final SignInModel signInModel = new SignInModel(userName, userPassword);
        final UserManager.ResponseCode responseCode = userManager.signInUser(signInModel);
        assertSame(responseCode, UserManager.ResponseCode.OK);
    }

    @Test
    public void signInUserWithIncorrectPassword() {
        final SignInModel signInModel = new SignInModel(userName, faker.internet().password());
        final UserManager.ResponseCode responseCode = userManager.signInUser(signInModel);
        assertSame(responseCode, UserManager.ResponseCode.INCORRECT_PASSWORD);
    }

    @Test
    public void signInUserThatNotExist() {
        final SignInModel signInModel = new SignInModel(faker.name().username(), userPassword);
        final UserManager.ResponseCode responseCode = userManager.signInUser(signInModel);
        assertSame(responseCode, UserManager.ResponseCode.INCORRECT_LOGIN);
    }

    @Test
    public void signUpOk() {
        final SignUpModel signUpModel = new SignUpModel(faker.name().username(), faker.internet().password(),
                faker.internet().emailAddress());
        final UserManager.ResponseCode responseCode = userManager.signUpUser(signUpModel);
        assertSame(responseCode, UserManager.ResponseCode.OK);
    }

    @Test
    public void signUpUserConflictUserName() {
        final SignUpModel signUpModel = new SignUpModel(userName, userPassword, userEmail);
        final UserManager.ResponseCode responseCode = userManager.signUpUser(signUpModel);
        assertSame(responseCode, UserManager.ResponseCode.LOGIN_IS_TAKEN);
    }

    @Test
    public void signUpUserConflictUserEmail() {
        final SignUpModel signUpModel = new SignUpModel(faker.name().username(), userPassword, userEmail);
        final UserManager.ResponseCode responseCode = userManager.signUpUser(signUpModel);
        assertSame(responseCode, UserManager.ResponseCode.EMAIL_IS_TAKEN);
    }

    @Test
    public void checkUserThatExist() {
        final boolean result = userManager.userExists(userName);
        assertSame(result, true);
    }

    @Test
    public void checkNullUser() {
        final boolean result = userManager.userExists(faker.name().username());
        assertSame(result, false);
    }

    @Test
    public void checkUserThatNotExist() {
        final boolean result = userManager.userExists(null);
        assertSame(result, false);
    }

    @Test
    public void changeUserEmailOk() {
        final UserManager.ResponseCode responseCode = userManager.changeUserEmail(faker.internet().emailAddress(),
                userName);
        assertSame(responseCode, UserManager.ResponseCode.OK);
    }

    @Test
    public void changeUserEmailWithExistingEmail() {
        final String otherUserName = faker.name().username();
        final String otherUserPassword = faker.internet().password();
        final String otherUseEmail = faker.internet().emailAddress();
        signUpUserOk(otherUserName,otherUserPassword,otherUseEmail);

        final UserManager.ResponseCode responseCode = userManager.changeUserEmail(userEmail,
                otherUserName);
        assertSame(responseCode, UserManager.ResponseCode.EMAIL_IS_TAKEN);
    }

    @SuppressWarnings("InstanceMethodNamingConvention")
    @Test
    public void changeUserEmailWithNotExistingUser() {
        final UserManager.ResponseCode responseCode = userManager.changeUserEmail(faker.internet().emailAddress(),
                faker.name().username());
        assertSame(responseCode, UserManager.ResponseCode.INCORRECT_LOGIN);
    }

    @Test
    public void changeUserPasswordOk() {
        final UserManager.ResponseCode responseCode = userManager.changeUserEmail(faker.internet().password(),
                userName);
        assertSame(responseCode, UserManager.ResponseCode.OK);
    }

    @SuppressWarnings("InstanceMethodNamingConvention")
    @Test
    public void changeUserPasswordWithNotExistingUser() {
        final UserManager.ResponseCode responseCode = userManager.changeUserEmail(faker.internet().password(),
                faker.name().username());
        assertSame(responseCode, UserManager.ResponseCode.INCORRECT_LOGIN);
    }

    @Test
    public void getUserByNameOk() {
        final UserModel userModel = new UserModel();
        final UserManager.ResponseCode responseCode = userManager.getUserByName(userName, userModel);
        assertSame(responseCode, UserManager.ResponseCode.OK);
        assertTrue(userModel.getUserName().equals(userName));
        assertTrue(userModel.getUserEmail().equals(userEmail));
    }

    @Test
    public void getUserByNameError() {
        final UserModel userModel = new UserModel();
        final UserManager.ResponseCode responseCode = userManager.getUserByName(faker.internet().emailAddress(),
                userModel);
        assertSame(responseCode, UserManager.ResponseCode.INCORRECT_LOGIN);
    }

    @Test
    public void changeUserHighScoreOk() {
        UserManager.ResponseCode responseCode = userManager.changeUserHighScore(userName, 100);
        assertSame(responseCode, UserManager.ResponseCode.OK);
    }

    @Test
    public void changeUserHighScoreError() {
        UserManager.ResponseCode responseCode = userManager.changeUserHighScore(faker.name().username(), 100);
        assertSame(responseCode, UserManager.ResponseCode.INCORRECT_LOGIN);
    }
}

