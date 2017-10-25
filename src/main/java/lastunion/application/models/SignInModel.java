package lastunion.application.models;


public class SignInModel {

    private final String userName;
    private final String userPassword;

    public SignInModel(String userName, String userPassword) {
        this.userName = userName;
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }
}
