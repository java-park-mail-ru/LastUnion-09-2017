package lastunion.application.Views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PasswordView implements AbstractView {
    private final String oldPassword;
    private final String newPassword;
    /**
     * @param oldPassword - string, contains old password
     * @param newPassword - string, contains new password
     */

    @SuppressWarnings("unused")
    @JsonCreator
    public PasswordView(@JsonProperty("oldPassword") String oldPassword,
                        @JsonProperty("newPassword") String newPassword){
        this.newPassword = newPassword;
        this.oldPassword = oldPassword;
    }


    public String getNewPassword() {
        return newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    @Override
    public boolean isFilled(){
        return newPassword != null && oldPassword != null;
    }
    @Override
    public boolean isValid() { return true;  }
}
