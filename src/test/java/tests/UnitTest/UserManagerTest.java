package tests.UnitTest;

import com.github.javafaker.Faker;
import lastunion.application.Application;
import lastunion.application.managers.UserManager;
import lastunion.application.models.SignInModel;
import lastunion.application.models.SignUpModel;
import lastunion.application.models.UserModel;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertSame;

@SuppressWarnings({"DefaultFileTemplate", "RedundantSuppression"})
@SpringBootTest(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)

public class UserManagerTest {
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

    public void signUpUserOk() {
        final SignUpModel signUpModel = new SignUpModel(userName, userPassword, userEmail);
        final UserManager.ResponseCode responseCode = userManager.signUpUser(signUpModel);
        assertSame(responseCode, UserManager.ResponseCode.OK);
    }

    @Before
    public void setUp() {
        userEmail = faker.internet().emailAddress();
        userName = faker.name().username();
        userPassword = faker.internet().password();
        signUpUserOk();
    }

    @Test
    public void checkPasswordByUserNameOk() {
        boolean result = userManager.checkPasswordByUserName(userPassword, userName);
        assertSame(result, true);
    }

    @Test
    public void checkPasswordByUserNameWithIncorrectPassword() {
        boolean result = userManager.checkPasswordByUserName(faker.internet().password(), userName);
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
    public void signUpUserConflict() {
        final SignUpModel signUpModel = new SignUpModel(userName, userPassword, userEmail);
        final UserManager.ResponseCode responseCode = userManager.signUpUser(signUpModel);
        assertSame(responseCode, UserManager.ResponseCode.LOGIN_IS_BUSY);
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

    //change email change password

    @Test
    public void changeUserEmailOk() {
        final UserManager.ResponseCode responseCode = userManager.changeUserEmail(faker.internet().emailAddress(),
                userName);
        assertSame(responseCode, UserManager.ResponseCode.OK);
    }

    @Test
    public void changeUserEmailWithNotExistingUser() {
        final UserManager.ResponseCode responseCode = userManager.changeUserEmail(faker.internet().emailAddress(),
                userName);
        assertSame(responseCode, UserManager.ResponseCode.INCORRECT_LOGIN);
    }

    @Test
    public void changeUserPasswordOk() {
        final UserManager.ResponseCode responseCode = userManager.changeUserEmail(faker.internet().password(),
                userName);
        assertSame(responseCode, UserManager.ResponseCode.OK);
    }

    @Test
    public void changeUserPasswordWithNotExistingUser() {
        final UserManager.ResponseCode responseCode = userManager.changeUserEmail(faker.internet().password(),
                faker.name().username());
        assertSame(responseCode, UserManager.ResponseCode.INCORRECT_LOGIN);
    }

    @Test
    public void getUserByNameOk() {
        UserModel userModel = new UserModel();
        final UserManager.ResponseCode responseCode = userManager.getUserByName(userName, userModel);
        assertSame(responseCode, UserManager.ResponseCode.OK);
        assertTrue(userModel.getUserName().equals(userName));
        assertTrue(userModel.getUserEmail().equals(userEmail));
    }

    @Test
    public void getUserByNameError() {
        UserModel userModel = new UserModel();
        final UserManager.ResponseCode responseCode = userManager.getUserByName(faker.internet().emailAddress(),
                userModel);
        assertSame(responseCode, UserManager.ResponseCode.INCORRECT_LOGIN);
    }
}

