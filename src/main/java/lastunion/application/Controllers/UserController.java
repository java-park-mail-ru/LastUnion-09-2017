package lastunion.application.Controllers;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lastunion.application.Managers.UserManager;
import lastunion.application.Views.ResponseCode;
import lastunion.application.Views.UserView;
import lastunion.application.Models.UserModel;
import lastunion.application.Views.PasswordView;
import lastunion.application.Views.EmailView;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.util.Locale;

@RestController
public class UserController {
    @NotNull
    private final MessageSource messageSource;
    @NotNull
    private final UserManager userManager;

    public UserController(@NotNull UserManager userManager,@NotNull MessageSource messageSource) {
        this.messageSource = messageSource;
        this.userManager = userManager;
    }

    @RequestMapping(path="/api/user/data", method= RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseCode<UserView>> getUserData(HttpSession httpSession){

        final String userName = (String)httpSession.getAttribute("userName");
        if (userName == null){
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.not_found", null, Locale.ENGLISH)),
            HttpStatus.NOT_FOUND);
        }

        final UserView userView = new UserView();
        final UserModel userModel = new UserModel();
        final UserManager.ResponseCode responseCode = userManager.getUserByName(userName, userModel);

        //noinspection EnumSwitchStatementWhichMissesCases
        switch(responseCode){
            case INCORRECT_LOGIN:{
                return new ResponseEntity<>(new ResponseCode<>(false,
                        messageSource.getMessage("msgs.forbidden", null, Locale.ENGLISH)),
                        HttpStatus.FORBIDDEN);
            }
            case OK:{
                // filling info about user
                userView.setUserId(userModel.getUserId());
                userView.setUserLogin(userModel.getUserName());
                userView.setUserEmail(userModel.getUserEmail());
                userView.setUserHighScore(userModel.getUserHighScore());

                return new ResponseEntity<>(new ResponseCode<>(true,
                        messageSource.getMessage("msgs.ok", null, Locale.ENGLISH),
                        userView), HttpStatus.OK);
            }
            default: {
                return new ResponseEntity<>(new ResponseCode<>(false,
                        messageSource.getMessage("msgs.internal_server_error", null, Locale.ENGLISH)),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @RequestMapping(path="/api/user/logout", method= RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseCode> logout(HttpSession httpSession){

        final String userName = (String)httpSession.getAttribute("userName");
        if (userName == null){
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.not_found", null, Locale.ENGLISH)),
                    HttpStatus.NOT_FOUND);
        }

        httpSession.invalidate();
        return new ResponseEntity<>(new ResponseCode<>(true,
                messageSource.getMessage("msgs.ok", null, Locale.ENGLISH)),
                HttpStatus.OK);
    }

    @RequestMapping(path="/api/user/change_email", method= RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE ,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseCode> changeEmail(@RequestBody EmailView emailView,
                                                    HttpSession httpSession ){
        // Check is there userName
        final String userName = (String)httpSession.getAttribute("userName");
        if (userName == null){
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.not_found", null, Locale.ENGLISH)),
                    HttpStatus.NOT_FOUND);
        }

        if (!emailView.isFilled()){
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.bad_request_json", null, Locale.ENGLISH)),
                    HttpStatus.BAD_REQUEST);
        }

        // check form for valid
        if (!emailView.isValid()) {
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.bad_request_form", null, Locale.ENGLISH)),
                    HttpStatus.BAD_REQUEST);
        }

        final UserManager.ResponseCode responseCode = userManager.changeUserEmail(emailView.getNewEmail(), userName);

        //noinspection EnumSwitchStatementWhichMissesCases
        switch (responseCode) {
            case INCORRECT_LOGIN: {//
                return new ResponseEntity<>(new ResponseCode<>(false,
                        messageSource.getMessage("msgs.not_found", null, Locale.ENGLISH)),
                        HttpStatus.NOT_FOUND);
            }
            case OK: {
                return new ResponseEntity<>(new ResponseCode<>(true,
                        messageSource.getMessage("msgs.ok", null, Locale.ENGLISH)),
                        HttpStatus.OK);
            }
            default: {
                return new ResponseEntity<>(new ResponseCode<>(false,
                        messageSource.getMessage("msgs.interanl_server_error", null, Locale.ENGLISH)),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }


    @RequestMapping(path="/api/user/change_password", method= RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseCode> changePassword(@RequestBody PasswordView passwordView,
                                                       HttpSession httpSession ){
        // Check is there userName
        final String userName = (String)httpSession.getAttribute("userName");

        if (!userManager.userExists(userName)){
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.not_found", null, Locale.ENGLISH)),
                    HttpStatus.NOT_FOUND);
        }


        if (!passwordView.isFilled()){
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.bad_request_json", null, Locale.ENGLISH)),
                    HttpStatus.BAD_REQUEST);
        }

        if (!passwordView.isValid()) {
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.bad_request_form", null, Locale.ENGLISH)),
                    HttpStatus.BAD_REQUEST);
        }

        if(!userManager.checkPasswordByUserName(passwordView.getOldPassword(), userName)) {
            return new ResponseEntity<>(new ResponseCode(false,
                    messageSource.getMessage("msgs.forbidden", null, Locale.ENGLISH)),
                    HttpStatus.FORBIDDEN);
        }

        final UserManager.ResponseCode responseCode = userManager.changeUserPassword(passwordView.getNewPassword(), userName);

        //noinspection EnumSwitchStatementWhichMissesCases
        switch(responseCode){
            case OK: {
                return new ResponseEntity<>(new ResponseCode<>(true,
                        messageSource.getMessage("msgs.ok", null, Locale.ENGLISH)),
                        HttpStatus.OK);
            }
            default: {
                return new ResponseEntity<>(new ResponseCode<>(false,
                        messageSource.getMessage("msgs.interanl_server_error", null, Locale.ENGLISH)),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @RequestMapping(path="/api/user/delete", method= RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseCode> deleteUser(HttpSession httpSession ){
        final String userName = (String)httpSession.getAttribute("userName");

        if (userName == null){
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.not_found", null, Locale.ENGLISH)),
                    HttpStatus.NOT_FOUND);
        }

        final UserManager.ResponseCode responseCode = userManager.deleteUserByName(userName);

        //noinspection EnumSwitchStatementWhichMissesCases
        switch (responseCode) {
            case OK:{
                httpSession.invalidate();
                return new ResponseEntity<>(new ResponseCode(true,
                        messageSource.getMessage("msgs.ok", null, Locale.ENGLISH)),
                        HttpStatus.OK);
            }
            default:{
                return new ResponseEntity<>(new ResponseCode(false,
                        messageSource.getMessage("msgs.internal_server_error", null, Locale.ENGLISH)),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
