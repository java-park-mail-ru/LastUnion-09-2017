package lastunion.application.controllers;

import lastunion.application.managers.UserManager;
import lastunion.application.models.SignUpModel;
import lastunion.application.views.ResponseCode;
import lastunion.application.views.SignUpView;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.Locale;

//@CrossOrigin(origins = "https://front-lastunion.herokuapp.com/")
@RestController
public class SignUpController {
    @NotNull
    private final MessageSource messageSource;
    @NotNull
    private final UserManager userManager;

    public SignUpController(@NotNull UserManager userManager, @NotNull MessageSource messageSource) {
        this.messageSource = messageSource;
        this.userManager = userManager;
    }

    @RequestMapping(path = "/api/user/signup", method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseCode> signUp(@RequestBody SignUpView signUpView, HttpSession httpSession) {

        if (!signUpView.isFilled()) {
            return new ResponseEntity<>(new ResponseCode(false,
                messageSource.getMessage("msgs.bad_request_json", null, Locale.ENGLISH)),
                HttpStatus.BAD_REQUEST);
        }
        // Incorrect reg data
        if (!signUpView.isValid()) {
            return new ResponseEntity<>(new ResponseCode(false,
                messageSource.getMessage("msgs.bad_request_form", null, Locale.ENGLISH)),
                HttpStatus.BAD_REQUEST);
        }

        final SignUpModel signUpUser = new SignUpModel(signUpView.getUserName(), signUpView.getUserPassword(),
            signUpView.getUserEmail());

        final UserManager.ResponseCode responseCode = userManager.signUpUser(signUpUser);
        //noinspection EnumSwitchStatementWhichMissesCases
        switch (responseCode) {
            case OK:
                httpSession.setAttribute("userLogin", signUpView.getUserName());
                return new ResponseEntity<>(new ResponseCode(true,
                    messageSource.getMessage("msgs.created", null, Locale.ENGLISH)),
                    HttpStatus.OK);

            case LOGIN_IS_TAKEN:
                return new ResponseEntity<>(new ResponseCode(false,
                    messageSource.getMessage("msgs.conflict_login", null, Locale.ENGLISH)),
                    HttpStatus.CONFLICT);

            case EMAIL_IS_TAKEN:
                return new ResponseEntity<>(new ResponseCode(false,
                        messageSource.getMessage("msgs.conflict_email", null, Locale.ENGLISH)),
                        HttpStatus.CONFLICT);

            default:
                return new ResponseEntity<>(new ResponseCode(false,
                    messageSource.getMessage("msgs.internal_server_error", null, Locale.ENGLISH)),
                    HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}
