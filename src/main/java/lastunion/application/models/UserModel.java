package lastunion.application.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public final class UserModel {
    @JsonIgnore
    private Integer userId;
    private String userName;
    @JsonIgnore
    private String userEmail;
    @JsonIgnore
    private String userPasswordHash;
    private Integer userHighScore;
    @JsonIgnore
    private Integer userCurrentScore;

    public UserModel() {
        userCurrentScore = 0;
    }

    public UserModel(Integer userId, String userName, String userEmail,
                     String userPasswordHash, Integer userHighScore) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPasswordHash = userPasswordHash;
        this.userHighScore = userHighScore;
        userCurrentScore = 0;

    }

    public UserModel(UserModel other) {
        this.userId = other.userId;
        this.userName = other.userName;
        this.userEmail = other.userEmail;
        this.userPasswordHash = other.userPasswordHash;
        this.userHighScore = other.userHighScore;
        this.userCurrentScore = other.userCurrentScore;
    }

    public UserModel(SignUpModel signUpModel) {
        this.userName = signUpModel.getUserName();
        this.userEmail = signUpModel.getUserEmail();
        this.userPasswordHash = signUpModel.getUserPassword();
    }



    @SuppressWarnings("unused")
    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPasswordHash() {
        return userPasswordHash;
    }

    public Integer getUserHighScore() {
        return userHighScore;
    }

    @SuppressWarnings("unused")
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserPasswordHash(String userPasswordHash) {
        this.userPasswordHash = userPasswordHash;
    }

    public void setUserHighScore(Integer userHighScore) {
        this.userHighScore = userHighScore;
    }
}
