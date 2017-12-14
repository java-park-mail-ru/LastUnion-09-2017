package lastunion.application.controllers;

import lastunion.application.views.ResponseCode;
import lastunion.application.views.GamepadView;
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
public class GamepadController {
    @NotNull
    private final MessageSource messageSource;

    public  GamepadController(@NotNull MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @RequestMapping(path = "/gamepad/add", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseCode> addGamepad(Locale locale, @RequestBody GamepadView gamepadView, HttpSession httpSession) {

        if (!gamepadView.isFilled()) {
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.bad_request_json", null, locale), null),
                    HttpStatus.BAD_REQUEST);
        }
        if (!gamepadView.isValid()) {
            return new ResponseEntity<>(new ResponseCode<>(false,
                    messageSource.getMessage("msgs.bad_request_form", null, locale), null),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseCode<>(false,
                messageSource.getMessage("12345", null, locale), null),
                HttpStatus.CREATED);
    }
}

