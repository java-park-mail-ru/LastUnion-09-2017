package lastunion.application.views;

import lastunion.application.models.UserModel;

import javax.validation.constraints.NotNull;

public class UserView implements AbstractView {
    private String userLogin;
    private String userEmail;
    private Integer userHighScore;

    public UserView() {
    }

    @SuppressWarnings("unused")
    public UserView(@NotNull UserModel userModel) {
        this.userLogin = userModel.getUserName();
        this.userEmail = userModel.getUserEmail();
        this.userHighScore = userModel.getUserHighScore();
    }

    @SuppressWarnings("unused")
    public String getUserLogin() {
        return userLogin;
    }

    @SuppressWarnings("unused")
    public String getUserEmail() {
        return userEmail;
    }

    @SuppressWarnings("unused")
    public Integer getUserHighScore() {
        return userHighScore;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserHighScore(Integer userHighScore) {
        this.userHighScore = userHighScore;
    }

    @Override
    @SuppressWarnings("OverlyComplexBooleanExpression")
    public final boolean isFilled() {
        return userLogin != null && userEmail != null && userHighScore != null;
    }

    @Override
    public final boolean isValid() {
        return true;
    }
}
