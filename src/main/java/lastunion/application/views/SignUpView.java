package lastunion.application.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class SignUpView implements AbstractView {
    private final String userName;
    private final String userPassword;
    private final String userEmail;

    @SuppressWarnings("unused")
    SignUpView(@JsonProperty("userName") String userName, @JsonProperty("userPassword") String userPassword,
               @JsonProperty("userEmail") String userEmail) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
    }


    public final String getUserName() {
        return userName;
    }

    public final String getUserPassword() {
        return userPassword;
    }

    public final String getUserEmail() {
        return userEmail;
    }

    @Override
    public final boolean isFilled() {
        return userEmail != null && userPassword != null && userName != null;
    }

    @Override
    public final boolean isValid() {
        if (!isFilled()) {
            return false;
        }
        try {
            final InternetAddress emailAddr = new InternetAddress(userEmail);
            emailAddr.validate();
        } catch (AddressException ex) {
            return false;
        }
        return true;
    }
}
