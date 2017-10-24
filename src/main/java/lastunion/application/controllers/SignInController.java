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

@CrossOrigin(origins = "${frontend_url}")
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
    public ResponseEntity<ResponseCode> signIn(Locale locale, @RequestBody SignInView signInView, HttpSession httpSession) {

        if (!signInView.isFilled()) {
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.bad_request_json", null, locale), null),
                    HttpStatus.BAD_REQUEST);
        }

        if (!signInView.isValid()) {
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.bad_request_form", null, locale), null),
                    HttpStatus.BAD_REQUEST);
        }
        final SignInModel signInUser = new SignInModel(signInView.getUserName(), signInView.getUserPassword());

        final UserManager.ResponseCode responseCode = userManager.signInUser(signInUser);
        //noinspection EnumSwitchStatementWhichMissesCases
        switch (responseCode) {

            case INCORRECT_LOGIN:
            case INCORRECT_PASSWORD:
                return new ResponseEntity<>(new ResponseCode<>(false,
                        messageSource.getMessage("msgs.forbidden", null,locale), null),
                        HttpStatus.FORBIDDEN);

            case OK:
                httpSession.setAttribute("userName", signInView.getUserName());
                return new ResponseEntity<>(new ResponseCode<>(true,
                        messageSource.getMessage("msgs.ok", null, locale), null),
                        HttpStatus.OK);

            default:
                return new ResponseEntity<>(new ResponseCode<>(false,
                        messageSource.getMessage("msgs.internal_server_error", null, locale), null),
                        HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}