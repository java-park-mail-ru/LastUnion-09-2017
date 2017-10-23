package lastunion.application.controllers;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lastunion.application.managers.UserManager;
import lastunion.application.views.ResponseCode;
import lastunion.application.views.UserView;
import lastunion.application.models.UserModel;
import lastunion.application.views.PasswordView;
import lastunion.application.views.EmailView;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.Locale;

@CrossOrigin(origins = "https://front-lastunion.herokuapp.com")
@RestController
public class UserController {
    @NotNull
    private final MessageSource messageSource;
    @NotNull
    private final UserManager userManager;

    public UserController(@NotNull UserManager userManager, @NotNull MessageSource messageSource) {
        this.messageSource = messageSource;
        this.userManager = userManager;
    }

    @RequestMapping(path = "/api/user/data", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseCode<UserView>> getUserData(HttpSession httpSession) {

        final String userName = (String) httpSession.getAttribute("userName");
        if (userName == null) {
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.not_found", null, Locale.ENGLISH), null),
                    HttpStatus.NOT_FOUND);
        }

        final UserView userView = new UserView();
        final UserModel userModel = new UserModel();
        final UserManager.ResponseCode responseCode = userManager.getUserByName(userName, userModel);

        //noinspection EnumSwitchStatementWhichMissesCases
        switch (responseCode) {
            case INCORRECT_LOGIN:
                return new ResponseEntity<>(new ResponseCode<>(false,
                        messageSource.getMessage("msgs.forbidden", null, Locale.ENGLISH), null),
                        HttpStatus.FORBIDDEN);

            case OK:
                // filling info about user
                userView.setUserLogin(userModel.getUserName());
                userView.setUserEmail(userModel.getUserEmail());
                userView.setUserHighScore(userModel.getUserHighScore());

                return new ResponseEntity<>(new ResponseCode<>(true,
                        messageSource.getMessage("msgs.ok", null, Locale.ENGLISH),
                        userView), HttpStatus.OK);

            default:
                return new ResponseEntity<>(new ResponseCode<>(false,
                        messageSource.getMessage("msgs.internal_server_error", null, Locale.ENGLISH), null),
                        HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @RequestMapping(path = "/api/user/logout", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseCode> logout(HttpSession httpSession) {

        final String userName = (String) httpSession.getAttribute("userName");
        if (userName == null) {
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.unauthorized", null, Locale.ENGLISH), null),
                    HttpStatus.UNAUTHORIZED);
        }

        httpSession.invalidate();
        return new ResponseEntity<>(new ResponseCode<>(true,
                messageSource.getMessage("msgs.ok", null, Locale.ENGLISH), null),
                HttpStatus.OK);
    }

    @RequestMapping(path = "/api/user/change_email", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseCode> changeEmail(@RequestBody EmailView emailView,
                                                    HttpSession httpSession) {
        // Check is there userName
        final String userName = (String) httpSession.getAttribute("userName");
        if (userName == null) {
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.not_found", null, Locale.ENGLISH), null),
                    HttpStatus.NOT_FOUND);
        }

        if (!emailView.isFilled()) {
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.bad_request_json", null, Locale.ENGLISH), null),
                    HttpStatus.BAD_REQUEST);
        }

        // check form for valid
        if (!emailView.isValid()) {
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.bad_request_form", null, Locale.ENGLISH), null),
                    HttpStatus.BAD_REQUEST);
        }

        final UserManager.ResponseCode responseCode = userManager.changeUserEmail(emailView.getNewEmail(), userName);

        //noinspection EnumSwitchStatementWhichMissesCases
        switch (responseCode) {
            case INCORRECT_LOGIN:
                return new ResponseEntity<>(new ResponseCode<>(false,
                        messageSource.getMessage("msgs.not_found", null, Locale.ENGLISH), null),
                        HttpStatus.NOT_FOUND);

            case EMAIL_IS_TAKEN:
                return new ResponseEntity<>(new ResponseCode<>(false,
                        messageSource.getMessage("msgs.conflict_email", null, Locale.ENGLISH), null),
                        HttpStatus.CONFLICT);

            case OK:
                return new ResponseEntity<>(new ResponseCode<>(true,
                        messageSource.getMessage("msgs.ok", null, Locale.ENGLISH), null),
                        HttpStatus.OK);

            default:
                return new ResponseEntity<>(new ResponseCode<>(false,
                        messageSource.getMessage("msgs.interanl_server_error", null, Locale.ENGLISH), null),
                        HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }


    @RequestMapping(path = "/api/user/change_password", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseCode> changePassword(@RequestBody PasswordView passwordView,
                                                       HttpSession httpSession) {
        // First part we check user
        final String userName = (String) httpSession.getAttribute("userName");
        if (userName == null) {
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.not_found", null, Locale.ENGLISH), null),
                    HttpStatus.NOT_FOUND);
        }

        final UserManager.ResponseCode responseCodeCheckUser = userManager.userExists(userName);
        switch (responseCodeCheckUser) {
            case INCORRECT_LOGIN:
                return new ResponseEntity<>(new ResponseCode<>(false,
                        messageSource.getMessage("msgs.not_found", null, Locale.ENGLISH), null),
                        HttpStatus.NOT_FOUND);
            case OK:
                break;

            default:
                return new ResponseEntity<>(new ResponseCode<>(false,
                        messageSource.getMessage("msgs.interanl_server_error", null, Locale.ENGLISH), null),
                        HttpStatus.INTERNAL_SERVER_ERROR);
        }


        if (!passwordView.isFilled()) {
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.bad_request_json", null, Locale.ENGLISH), null),
                    HttpStatus.BAD_REQUEST);
        }

        if (!passwordView.isValid()) {
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.bad_request_form", null, Locale.ENGLISH), null),
                    HttpStatus.BAD_REQUEST);
        }

        final UserManager.ResponseCode responseCodeCheckPassword = userManager.checkPasswordByUserName(passwordView.getOldPassword(), userName);
        switch (responseCodeCheckPassword) {
            case INCORRECT_PASSWORD:
                return new ResponseEntity<>(new ResponseCode<>(false,
                        messageSource.getMessage("msgs.forbidden", null, Locale.ENGLISH), null),
                        HttpStatus.FORBIDDEN);
            case OK:
                break;

            default:
                return new ResponseEntity<>(new ResponseCode<>(false,
                        messageSource.getMessage("msgs.interanl_server_error", null, Locale.ENGLISH), null),
                        HttpStatus.INTERNAL_SERVER_ERROR);
        }

        final UserManager.ResponseCode responseCode = userManager.changeUserPassword(passwordView.getNewPassword(), userName);
        //noinspection EnumSwitchStatementWhichMissesCases
        switch (responseCode) {
            case OK:
                return new ResponseEntity<>(new ResponseCode<>(true,
                        messageSource.getMessage("msgs.ok", null, Locale.ENGLISH), null),
                        HttpStatus.OK);

            default:
                return new ResponseEntity<>(new ResponseCode<>(false,
                        messageSource.getMessage("msgs.interanl_server_error", null, Locale.ENGLISH), null),
                        HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @RequestMapping(path = "/api/user/delete", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseCode> deleteUser(HttpSession httpSession) {
        final String userName = (String) httpSession.getAttribute("userName");

        if (userName == null) {
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.not_found", null, Locale.ENGLISH), null),
                    HttpStatus.NOT_FOUND);
        }

        final UserManager.ResponseCode responseCode = userManager.deleteUserByName(userName);

        //noinspection EnumSwitchStatementWhichMissesCases
        switch (responseCode) {
            case OK:
                httpSession.invalidate();
                return new ResponseEntity<>(new ResponseCode<>(true,
                        messageSource.getMessage("msgs.ok", null, Locale.ENGLISH), null),
                        HttpStatus.OK);

            default:
                return new ResponseEntity<>(new ResponseCode<>(false,
                        messageSource.getMessage("msgs.internal_server_error", null, Locale.ENGLISH), null),
                        HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @RequestMapping(path = "/api/user/score", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getHighScore(HttpSession httpSession) {
        final String userName = (String) httpSession.getAttribute("userName");
        if (userName == null) {
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.not_found", null, Locale.ENGLISH), null),
                    HttpStatus.NOT_FOUND);
        }
        final UserModel userModel = new UserModel();
        userManager.getUserByName(userName, userModel);
        return new ResponseEntity<>(new ResponseCode<>(true,
                messageSource.getMessage("msgs.ok", null, Locale.ENGLISH),
                userModel.getUserHighScore()), HttpStatus.OK);
    }
}
