package tests.Test;

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

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("UnnecessaryFullyQualifiedName")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Category(tests.IntegrationTest.class)
public class SignInTest {
    @Autowired
    private MockMvc mock;
    private static TestRequestBuilder requestBuilder;
    private static Faker faker;
    private static String pathUrl;
    private static String userName;
    private static String userEmail;
    private static String userPassword;


    @SuppressWarnings("MissortedModifiers")
    @BeforeClass
    static public void init() {
        faker = new Faker();
        requestBuilder = new TestRequestBuilder();
        requestBuilder.init("userName", "userPassword");
    }

//    private static String getJsonRequest(String uName, String uPassword, String uEmail, boolean emailNeeds ){
//        final JSONObject jso = new JSONObject();
//        jso.put("userName", uName);
//        jso.put("userPassword", uPassword);
//        if (emailNeeds)
//            jso.put("userEmail", uEmail);
//        return jso.toString();
//    }


    public void createUser() throws Exception {
        this.mock.perform(
                post("/api/user/signup")
                        .contentType("application/json")//MediaType.APPLICATION_JSON_VALUE)
                        .content(TestRequestBuilder.getJsonRequestForSignUp(userName, userPassword, userEmail)))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(true)))
                .andExpect(jsonPath("$.responseMessage", is("User created successfully! en")));
    }

    @SuppressWarnings("ThrowInsideCatchBlockWhichIgnoresCaughtException")
    @Before
    public void setUp() {
        userName = faker.name().username();
        userEmail = faker.internet().emailAddress();
        userPassword = faker.internet().password();
        pathUrl = "/api/user/signin";

        try {
            createUser();
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }


    @Test
    public void signInNormal() throws Exception {
        this.mock.perform(
                post(pathUrl)
                        .contentType("application/json")
                        .content(requestBuilder.getJsonRequest(userName, userPassword)))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(true)))
                .andExpect(jsonPath("$.responseMessage", is("Ok! en")));
    }

    @Test
    public void signInNullUserName() throws Exception {
        this.mock.perform(
                post(pathUrl)
                        .contentType("application/json")
                        .content(requestBuilder.getJsonRequest(null, userPassword)))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.responseMessage", is("Json contains null fields! en")));
    }

    @Test
    public void signInNullUserPassword() throws Exception {
        this.mock.perform(
                post(pathUrl)
                        .contentType("application/json")
                        .content(requestBuilder.getJsonRequest(userName, null)))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.responseMessage", is("Json contains null fields! en")));
    }


    @Test
    public void signInIncorrectUserName() throws Exception {
        this.mock.perform(
                post(pathUrl)
                        .contentType("application/json")
                        .content(requestBuilder.getJsonRequest("Petya", userPassword)))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.responseMessage", is("Invalid authentication data! en")));
    }


    @Test
    public void signInIncorrectUserPassword() throws Exception {
        this.mock.perform(
                post(pathUrl)
                        .contentType("application/json")
                        .content(requestBuilder.getJsonRequest(userName, "no")))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.result", is(false)))
                .andExpect(jsonPath("$.responseMessage", is("Invalid authentication data! en")));
    }


    @Test
    public void signInIncorrectDocumentType() throws Exception {
        this.mock.perform(
                post(pathUrl)
                        .contentType("text/html"))
                .andExpect(status().isUnsupportedMediaType());
    }
}
