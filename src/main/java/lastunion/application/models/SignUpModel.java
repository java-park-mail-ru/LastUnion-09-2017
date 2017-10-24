package lastunion.application.models;


public class SignUpModel {
    private final String userName;
    private final String userPassword;
    private final String userEmail;

    public SignUpModel(String userName, String userPassword,String userEmail) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
