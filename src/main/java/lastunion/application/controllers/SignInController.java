package lastunion.application.controllers;

import lastunion.application.managers.UserManager;
import lastunion.application.models.SignInModel;
import lastunion.application.views.ResponseCode;
import lastunion.application.views.SignInView;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.Locale;

@CrossOrigin(origins = "https://front-lastunion.herokuapp.com/")
@RestController
public class SignInController {
    @NotNull
    private final MessageSource messageSource;
    @NotNull
    private final UserManager userManager;

    public SignInController(@NotNull UserManager userManager, @NotNull MessageSource messageSource) {
        this.messageSource = messageSource;
        this.userManager = userManager;
    }

    @RequestMapping(path = "/api/user/signin", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseCode> signIn(@RequestBody SignInView signInView, HttpSession httpSession) {

        if (!signInView.isFilled()) {
            return new ResponseEntity<>(new ResponseCode(false,
                    messageSource.getMessage("msgs.bad_request_json", null, Locale.ENGLISH)),
                    HttpStatus.BAD_REQUEST);
        }

        // Incorrect authenticatiion data
        if (!signInView.isValid()) {
            return new ResponseEntity<>(new ResponseCode(false,
                    messageSource.getMessage("msgs.bad_request_form", null, Locale.ENGLISH)),
                    HttpStatus.BAD_REQUEST);
        }
        final SignInModel signInUser = new SignInModel(signInView.getUserName(), signInView.getUserPassword());

        final UserManager.ResponseCode responseCode = userManager.signInUser(signInUser);
        //noinspection EnumSwitchStatementWhichMissesCases
        switch (responseCode) {

            case INCORRECT_LOGIN:
            case INCORRECT_PASSWORD:
                return new ResponseEntity<>(new ResponseCode(false,
                        messageSource.getMessage("msgs.forbidden", null, Locale.ENGLISH)),
                        HttpStatus.FORBIDDEN);

            case OK:
                httpSession.setAttribute("userLogin", signInView.getUserName());
                return new ResponseEntity<>(new ResponseCode(true,
                        messageSource.getMessage("msgs.ok", null, Locale.ENGLISH)),
                        HttpStatus.OK);

            default:
                return new ResponseEntity<>(new ResponseCode(false,
                        messageSource.getMessage("msgs.internal_server_error", null, Locale.ENGLISH)),
                        HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}