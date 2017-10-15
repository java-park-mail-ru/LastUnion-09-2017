package lastunion.application.models;

public final class UserModel {
    private Integer userId;
    private String userName;
    private String userEmail;
    private String userPasswordHash;
    private Integer userHighScore;

    public UserModel() {
    }

    public UserModel(Integer userId, String userName, String userEmail,
                     String userPasswordHash, Integer userHighScore) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPasswordHash = userPasswordHash;
        this.userHighScore = userHighScore;
    }

    public UserModel(UserModel other) {
        this.userId = other.userId;
        this.userName = other.userName;
        this.userEmail = other.userEmail;
        this.userPasswordHash = other.userPasswordHash;
        this.userHighScore = other.userHighScore;
    }

    public UserModel(SignUpModel signUpModel) {
        this.userName = signUpModel.getUserName();
        this.userEmail = signUpModel.getUserEmail();
        this.userPasswordHash = signUpModel.getUserPassword();
    }

    /*
    Getters and Setters
     */

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
