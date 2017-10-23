package tests.api;

import com.github.javafaker.Faker;
import lastunion.application.Application;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("UnnecessaryFullyQualifiedName")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
public class ChangePaswordIntTest {
    @Autowired
    private MockMvc mock;
    private static Faker faker;
    private static TestRequestBuilder requestBuilder;
    private static String pathUrl;
    private static String userName;
    private static String userEmail;
    private static String userPassword;



    @SuppressWarnings("MissortedModifiers")
    @BeforeClass
    static public void init(){
        faker = new Faker();
        requestBuilder = new TestRequestBuilder();
        requestBuilder.init("oldPassword", "newPassword");
    }

    public void createUser() throws Exception {
        this.mock.perform(
                post("/api/user/signup")
                        .contentType("application/json")
                        .content(TestRequestBuilder.getJsonRequestForSignUp(userName, userPassword, userEmail)))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result", is(true)))
                .andExpect(jsonPath("$.responseMessage", is("User created successfully! en")));
    }

    @Before
    public void setUp() throws Exception {
        userName = faker.name().username();
        userEmail = faker.internet().emailAddress();
        userPassword = faker.internet().password();
        pathUrl = "/api/user/change_password";

        createUser();
    }

    @Test
    public void changePasswordNormal() throws Exception{
        this.mock.perform(
                post(pathUrl)
                        .contentType("application/json")
                        .content(requestBuilder.getJsonRequest(userPassword, faker.internet().password()))
                        .sessionAttr("userName", userName))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(true)))
                .andExpect(jsonPath("$.responseMessage", is("Ok! en")));
    }

    @SuppressWarnings("InstanceMethodNamingConvention")
    @Test
    public void changePasswordIncorrectOldPassword() throws Exception {
        this.mock.perform(
                post(pathUrl)
                        .contentType("application/json")
                        .content(requestBuilder.getJsonRequest("aaa", faker.internet().password()))
                        .sessionAttr("userName", userName))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.responseMessage", is("Invalid authentication data! en")));
    }


    @Test
    public void changePasswordNullOldPassword() throws Exception{
        this.mock.perform(
                post(pathUrl)
                        .contentType("application/json")
                        .content(requestBuilder.getJsonRequest(null, faker.internet().password()))
                        .sessionAttr("userName", userName))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.responseMessage", is("Json contains null fields! en")));
    }

    @Test
    public void changePasswordNullNewPassword() throws Exception{
        this.mock.perform(
                post(pathUrl)
                        .contentType("application/json")
                        .content(requestBuilder.getJsonRequest(userPassword, null))
                        .sessionAttr("userName", userName))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.responseMessage", is("Json contains null fields! en")));
    }

    @Test
    public void changePasswordNullBothPasswords() throws Exception{
        this.mock.perform(
                post(pathUrl)
                        .contentType("application/json")
                        .content(requestBuilder.getJsonRequest(null, null))
                        .sessionAttr("userName", userName))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.responseMessage", is("Json contains null fields! en")));
    }

    @Test
    public void changePasswordNullSession() throws Exception{
        this.mock.perform(
                post(pathUrl)
                        .contentType("application/json")
                        .content(requestBuilder.getJsonRequest(userPassword, faker.internet().password())))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.responseMessage", is("Invalid session! en")));
    }

    @Test
    public void changePasswordInvalidSession() throws Exception {
        this.mock.perform(
                post(pathUrl)
                        .contentType("application/json")
                        .content(requestBuilder.getJsonRequest(userPassword, faker.internet().password()))
                        .sessionAttr("userName", faker.name().username()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.responseMessage", is("Invalid session! en")));
    }

    @SuppressWarnings("InstanceMethodNamingConvention")
    @Test
    public void changePasswordIncorrectDocumentType() throws Exception{
        this.mock.perform(
                post(pathUrl)
                        .contentType("text/html"))
                .andExpect(status().isUnsupportedMediaType());
    }
}