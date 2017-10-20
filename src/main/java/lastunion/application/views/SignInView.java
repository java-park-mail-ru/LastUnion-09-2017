package lastunion.application.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SignInView implements AbstractView {
    private final String userName;
    private final String userPassword;

    @SuppressWarnings("unused")
    SignInView(@JsonProperty("userName") String userName,
               @JsonProperty("userPassword") String userPassword) {
        this.userName = userName;
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    @Override
    public boolean isFilled() {
        return userName != null && userPassword != null;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
