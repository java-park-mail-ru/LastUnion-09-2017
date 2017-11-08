package tests.api;

import com.github.javafaker.Faker;
import lastunion.application.Application;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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

@SuppressWarnings({"UnnecessaryFullyQualifiedName", "RedundantSuppression"})
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
public class SignUpIntTest {
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
    static public void init() {
        faker = new Faker();
        requestBuilder = new TestRequestBuilder();
        requestBuilder.init("userName", "userPassword", "userEmail");
    }


    public void createUser() throws Exception {
        this.mock.perform(
                post("/api/user/signup")
                        .contentType("application/json")//MediaType.APPLICATION_JSON_VALUE)
                        .content(TestRequestBuilder.getJsonRequestForSignUp(userName, userPassword, userEmail)))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result", is(true)))
                .andExpect(jsonPath("$.responseMessage", is("User created successfully!")));
    }

    @Before
    public void setUp() throws Exception {
        userName = faker.name().username();
        userEmail = faker.internet().emailAddress();
        userPassword = faker.internet().password();
        pathUrl = "/api/user/signup";

        createUser();
    }

    @Test
    public void signUpNormal() throws Exception {
        this.mock.perform(
                post(pathUrl)
                        .contentType("application/json")
                        .content(requestBuilder.getJsonRequest(faker.name().username(),
                                faker.internet().password(),
                                faker.internet().emailAddress())))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result", is(true)))
                .andExpect(jsonPath("$.responseMessage", is("User created successfully!")));
    }


    @Test
    public void signUpConflictUserName() throws Exception {
        this.mock.perform(
                post(pathUrl)
                        .contentType("application/json")
                        .content(requestBuilder.getJsonRequest(userName, userPassword, userEmail)))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.responseMessage", is("Login already occupied!")));
    }

    @Test
    public void signUpConflictUserEmail() throws Exception {
        final String otherUserName = faker.name().username();
        final String otherUserPassword = faker.internet().password();

        this.mock.perform(
                post(pathUrl)
                        .contentType("application/json")
                        .content(requestBuilder.getJsonRequest(otherUserName, otherUserPassword, userEmail)))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.responseMessage", is("Email already registered!")));
    }


    @Test
    public void signUpNullUserName() throws Exception {
        this.mock.perform(
                post(pathUrl)
                        .contentType("application/json")
                        .content(requestBuilder.getJsonRequest(null, userPassword, userEmail)))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.responseMessage", is("Json contains null fields!")));
    }

    @Test
    public void signUpNullUserPassword() throws Exception {
        this.mock.perform(
                post(pathUrl)
                        .contentType("application/json")
                        .content(requestBuilder.getJsonRequest(userName, null, userEmail)))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.responseMessage", is("Json contains null fields!")));
    }


    @Test
    public void signUpNullUserEmail() throws Exception {
        this.mock.perform(
                post(pathUrl)
                        .contentType("application/json")
                        .content(requestBuilder.getJsonRequest(userName, userPassword, null)))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.responseMessage", is("Json contains null fields!")));
    }


    @Test
    public void signUpIncorrectDocumentType() throws Exception {
        this.mock.perform(
                post(pathUrl)
                        .contentType("text/html"))
                .andExpect(status().isUnsupportedMediaType());
    }
}
