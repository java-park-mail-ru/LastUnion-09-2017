package lastunion.application.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;


public final class EmailView implements AbstractView {
    private String newEmail;

    @SuppressWarnings("unused")
    public EmailView(@JsonProperty("userEmail") String newUserEmail) {
        this.newEmail = newUserEmail;
    }

    public String getNewEmail() {
        return newEmail;
    }

    @SuppressWarnings("unused")
    public void setNewEmail(String newUserEmail) {
        this.newEmail = newUserEmail;
    }

    @Override
    public boolean isFilled() {
        return newEmail != null;
    }

    @Override
    public boolean isValid() {
        if (!isFilled()) {
            return false;
        }
        if (!isFilled()) {
            return false;
        }
        try {
            final InternetAddress emailAddr = new InternetAddress(newEmail);
            emailAddr.validate();
        } catch (AddressException ex) {
            return false;
        }
        return true;
    }
}
