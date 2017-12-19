package lastunion.application.game.views;

@SuppressWarnings("DefaultFileTemplate")
public class UserGameView {
    private final String UserName;

    public UserGameView(String UserName) {
        this.UserName = UserName;
    }

    public String getUserId() {
        return this.UserName;
    }

    @SuppressWarnings("unused")
    public String getUserName() {
        return this.UserName;
    }
}
